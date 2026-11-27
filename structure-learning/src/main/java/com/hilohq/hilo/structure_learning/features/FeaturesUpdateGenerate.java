package com.hilohq.hilo.structure_learning.features;

import static com.hilohq.hilo.features.stats.feature.FeatureProtobufObjectMapper.*;

import com.hilohq.hilo.features.stats.feature.FeatureInfer;
import com.hilohq.hilo.features.stats.feature.FeatureStats;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.HashMap;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

/**
 * Merges the feature statistics of a run with the features already stored for the schema, infers
 * each feature's class, and emits the updated features plus a log entry for every feature that is
 * new ({@code featureCreate}) or whose class changed ({@code featureUpdate}).
 */
public final class FeaturesUpdateGenerate
    extends RichGroupReduceFunction<
        FeatureOuterClass.FeaturesEvent, FeatureOuterClass.FeaturesUpdate> {

  private final FeaturesLoader featuresLoader;

  /** Treats every run as a fresh feature set (no previously stored features). */
  public FeaturesUpdateGenerate() {
    this(FeaturesLoader.none());
  }

  public FeaturesUpdateGenerate(final FeaturesLoader featuresLoader) {
    this.featuresLoader = featuresLoader;
  }

  @Override
  public void reduce(
      final Iterable<FeatureOuterClass.FeaturesEvent> values,
      final Collector<FeatureOuterClass.FeaturesUpdate> out)
      throws Exception {
    final var newGroupStats = new HashMap<String, FeatureStats>();
    final var featureInfer = new FeatureInfer();
    FeatureOuterClass.FeaturesMetadata metadata = null;

    for (final var event : values) {
      if (!event.hasStats()) {
        continue;
      }

      final var key = event.getStats().getKey();

      final var stats = FeatureStats.fromProtobuf(event.getStats().getFeatureStats());

      if (newGroupStats.containsKey(key)) {
        newGroupStats.get(key).merge(stats);
      } else {
        newGroupStats.put(key, stats);
      }
      metadata = event.getMetadata();
    }

    if (metadata == null) {
      return;
    }

    final var currentFeatures = featuresLoader.load(metadata.getSchemaId());

    for (final var entry : newGroupStats.entrySet()) {
      final var key = entry.getKey();
      final var isNew = !currentFeatures.containsKey(key);
      if (!isNew) {
        entry.getValue().merge(FeatureStats.fromProtobuf(currentFeatures.get(key).getStats()));
      }
      final var newFeatureClass = featureInfer.infer(entry.getValue());

      if (isNew) {
        out.collect(createFeatureCreateUpdate(metadata, key, newFeatureClass));

      } else if (!newFeatureClass.equals(currentFeatures.get(key).getFeatureClass())) {
        out.collect(
            createFeatureUpdateUpdate(
                metadata, key, currentFeatures.get(key).getFeatureClass(), newFeatureClass));
      }

      out.collect(
          FeatureOuterClass.FeaturesUpdate.newBuilder()
              .setMetadata(metadata)
              .setFeature(
                  FeatureOuterClass.Feature.newBuilder()
                      .setKey(key)
                      .setStats(newGroupStats.get(key).snapshot())
                      .setFeatureClass(newFeatureClass))
              .build());
    }
  }
}
