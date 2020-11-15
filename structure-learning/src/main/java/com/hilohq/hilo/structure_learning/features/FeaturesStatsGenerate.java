package com.hilohq.hilo.structure_learning.features;

import com.hilohq.hilo.features.stats.feature.FeatureGroupStats;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;
import com.hilohq.hilo.protobufs.structure.Schema;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

public final class FeaturesStatsGenerate
    extends RichGroupReduceFunction<Fields<FieldKey>, FeatureOuterClass.FeaturesEvent> {

  @Override
  public void reduce(
      final Iterable<Fields<FieldKey>> values, final Collector<FeatureOuterClass.FeaturesEvent> out)
      throws Exception {
    final var stats = new FeatureGroupStats();
    Schema.SchemaMetadata metadata = null;

    for (final var fields : values) {
      final var map = fields.map();
      metadata = fields.metadata();
      for (final var entry : map.entrySet()) {
        stats.observe(entry.getKey().key(), entry.getValue());
      }
    }

    for (final var entry : stats.featureStats()) {
      out.collect(
          FeatureOuterClass.FeaturesEvent.newBuilder()
              .setMetadata(
                  FeatureOuterClass.FeaturesMetadata.newBuilder()
                      .setSchemaId(metadata.getSchemaId())
                      .setProjectId(metadata.getProjectId())
                      .setSourceId(metadata.getSourceId()))
              .setStats(
                  FeatureOuterClass.StatsEvent.newBuilder()
                      .setKey(entry.getKey())
                      .setFeatureStats(entry.getValue().snapshot()))
              .build());
    }
  }
}
