package com.hilohq.hilo.structure_learning.schema.json;

import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonInfer;
import com.hilohq.hilo.parsers.logparse.json.JsonType;
import com.hilohq.hilo.parsers.logparse.log.TypeInfoStats;
import com.hilohq.hilo.protobufs.structure.Json;
import com.hilohq.hilo.protobufs.structure.Schema;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.util.Collector;

public final class JsonSchemaStatsGenerate
    implements GroupReduceFunction<String, Schema.SchemaEvent> {

  private final String projectId;
  private final String sourceId;
  private final String schemaId;

  public JsonSchemaStatsGenerate(
      final String projectId, final String sourceId, final String schemaId) {
    this.projectId = projectId;
    this.sourceId = sourceId;
    this.schemaId = schemaId;
  }

  @Override
  public void reduce(final Iterable<String> values, final Collector<Schema.SchemaEvent> out)
      throws Exception {
    final var infer = new JsonInfer();
    final var stats = new TypeInfoStats<JsonFieldKey, JsonType>();

    for (final var value : values) {
      final var typeInfo = infer.infer(value);
      for (final var entry : typeInfo.typeInfo().entrySet()) {
        stats.observe(entry.getKey(), entry.getValue());
      }
    }

    for (final var fieldStats : stats.fieldStats()) {
      final var event =
          JsonProtobufUtils.createJsonFieldStats(
              fieldStats.getKey().path(), fieldStats.getKey().name(), fieldStats.getValue());
      out.collect(
          Schema.SchemaEvent.newBuilder()
              .setMetadata(
                  Schema.SchemaMetadata.newBuilder()
                      .setProjectId(projectId)
                      .setSchemaId(schemaId)
                      .setSourceId(sourceId))
              .setJson(Json.JsonEvent.newBuilder().setFieldStats(event))
              .build());
    }
  }
}
