package com.hilohq.hilo.structure_learning.features;

import static com.hilohq.hilo.features.stats.feature.FeatureProtobufObjectMapper.*;

import com.hilohq.hilo.features.stats.feature.FeatureInfer;
import com.hilohq.hilo.features.stats.feature.FeatureStats;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.HashMap;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

public final class FeaturesUpdateGenerate
    extends RichGroupReduceFunction<
        FeatureOuterClass.FeaturesEvent, FeatureOuterClass.FeaturesUpdate> {
  @Override
  public void reduce(
      final Iterable<FeatureOuterClass.FeaturesEvent> values,
      final Collector<FeatureOuterClass.FeaturesUpdate> out)
      throws Exception {
    final var currentFeatures = new HashMap<String, FeatureOuterClass.Feature>();
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

    for (final var entry : newGroupStats.entrySet()) {
      final var key = entry.getKey();
      final var isNew = !currentFeatures.containsKey(key);
      if (!isNew) {
        entry.getValue().merge(FeatureStats.fromProtobuf(currentFeatures.get(key).getStats()));
      }
      final var newFeatureClass = featureInfer.infer(entry.getValue());

      if (isNew) {
        out.collect(createFeatureCreateUpdate(metadata, key, newFeatureClass));

      } else if (newFeatureClass.equals(currentFeatures.get(key).getFeatureClass())) {
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
