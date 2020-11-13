package com.hilohq.hilo.structure_learning.features;

import com.hilohq.hilo.features.stats.feature.FeatureGroupStats;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

public final class FeatureStatsGenerate
    extends RichGroupReduceFunction<FieldsMap<FieldKey>, FeatureOuterClass.FeaturesEvent> {

  @Override
  public void reduce(
      final Iterable<FieldsMap<FieldKey>> values,
      final Collector<FeatureOuterClass.FeaturesEvent> out)
      throws Exception {
    final var stats = new FeatureGroupStats();

    for (final var fields : values) {
      for (final var entry : fields.entrySet()) {
        stats.observe(entry.getKey().key(), entry.getValue());
      }
    }

    for (final var entry : stats.featureStats()) {
      out.collect(
          FeatureOuterClass.FeaturesEvent.newBuilder()
              .setStats(
                  FeatureOuterClass.FeaturesEvent.Stats.newBuilder()
                      .setKey(entry.getKey())
                      .setFeatureStats(entry.getValue().snapshot()))
              .build());
    }
  }
}
