package com.hilohq.hilo.structure_learning;

import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.hilohq.hilo.structure_learning.features.FeaturesStatsGenerate;
import com.hilohq.hilo.structure_learning.features.FeaturesUpdateGenerate;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.flink.api.common.functions.util.ListCollector;

/**
 * Produces the {@code features.json} golden files by running the real feature-learning reduce
 * functions in-process on a sample, the same way {@code JsonFeaturesGenerateStages} chains them.
 *
 * <p>Run {@code ./gradlew :structure-learning:regenerateGoldenFeatures} after an intentional change
 * to feature learning, then review the diff of the golden files.
 */
public final class GoldenFeatures {
  private GoldenFeatures() {}

  /** Runs FeaturesStatsGenerate and FeaturesUpdateGenerate on the validated sample logs. */
  public static List<FeatureOuterClass.FeaturesUpdate> updates(final String sample)
      throws Exception {
    final var events = new ArrayList<FeatureOuterClass.FeaturesEvent>();
    new FeaturesStatsGenerate()
        .reduce(Samples.validatedFields(sample), new ListCollector<>(events));

    final var updates = new ArrayList<FeatureOuterClass.FeaturesUpdate>();
    new FeaturesUpdateGenerate().reduce(events, new ListCollector<>(updates));
    return updates;
  }

  /**
   * Builds the features model the pipeline stores in Mongo, in the golden-file format: metadata
   * placeholders instead of ids, no per-feature {@code key} (it is the map key), and features in
   * the order of the existing golden file so regenerated files diff cleanly.
   */
  public static FeatureOuterClass.FeaturesModel model(final String sample) throws Exception {
    final var features = new LinkedHashMap<String, FeatureOuterClass.Feature>();
    for (final var update : updates(sample)) {
      if (update.hasFeature()) {
        features.put(
            update.getFeature().getKey(), update.getFeature().toBuilder().clearKey().build());
      }
    }

    final var ordered = new LinkedHashMap<String, FeatureOuterClass.Feature>();
    for (final var key : existingKeyOrder(sample)) {
      if (features.containsKey(key)) {
        ordered.put(key, features.remove(key));
      }
    }
    ordered.putAll(features);

    return FeatureOuterClass.FeaturesModel.newBuilder()
        .setMetadata(
            FeatureOuterClass.FeaturesMetadata.newBuilder()
                .setSchemaId("$SCHEMA_ID")
                .setSourceId("$SOURCE_ID")
                .setProjectId("$PROJECT_ID"))
        .putAllFeatures(ordered)
        .build();
  }

  private static List<String> existingKeyOrder(final String sample) {
    try {
      return new ArrayList<>(
          Samples.readProto(
                  sample, "features.json", FeatureOuterClass.FeaturesModel.getDefaultInstance())
              .getFeaturesMap()
              .keySet());
    } catch (RuntimeException e) {
      return List.of();
    }
  }

  public static void main(final String[] args) throws Exception {
    final var samples = args.length > 0 ? List.of(args) : List.of("blocks", "transactions");
    for (final var sample : samples) {
      final var path = Samples.path(sample, "features.json");
      final var previous = Files.exists(path) ? Files.readString(path) : null;
      final var json = GoldenJson.format(JsonFormat.printer().print(model(sample)), previous);
      Files.writeString(path, json);
      System.out.println("wrote " + path.toAbsolutePath().normalize());
    }
  }
}
