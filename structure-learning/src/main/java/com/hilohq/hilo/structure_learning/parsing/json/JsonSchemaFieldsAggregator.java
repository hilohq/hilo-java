package com.hilohq.hilo.structure_learning.parsing.json;

import com.hilohq.hilo.protobufs.parsing.Json;
import com.hilohq.hilo.protobufs.parsing.Schema;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonFieldStats;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonSchemaStats;
import com.hilohq.hilo.structure_learning.parsing.json.entity.ProtobufMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public final class JsonSchemaFieldsAggregator
    extends ProcessFunction<Schema.SchemaEvent, Schema.SchemaUpdate> {

  private final JsonSchemaStats schemaStats;
  private final JsonSchema schema;

  public JsonSchemaFieldsAggregator() {
    this.schemaStats = new JsonSchemaStats();
    this.schema = new JsonSchema();
  }

  private static Json.JsonType calculateType(final JsonFieldStats stats) {
    var maxCount = 0L;
    var maxType = Json.JsonType.NULL;
    for (final var countEntries : stats.typeHistogram().counts().entrySet()) {
      if (countEntries.getValue() > maxCount) {
        maxCount = countEntries.getValue();
        maxType = countEntries.getKey();
      }
    }

    return maxType;
  }

  private List<Schema.SchemaUpdateData> createNewField(final Json.JsonFieldStats stats) {
    final var path = stats.getPath();
    final var name = stats.getName();
    schemaStats.put(ProtobufMapper.fromProtobuf(stats));
    final var type = calculateType(schemaStats.get(path, name));
    schema.put(path, name, type);
    final var field = schema.get(path, name);

    return Arrays.asList(
        Schema.SchemaUpdateData.newBuilder()
            .setFields(
                Schema.SchemaFields.newBuilder()
                    .setJson(Json.JsonSchema.newBuilder().addFields(field).build()))
            .build(),
        Schema.SchemaUpdateData.newBuilder()
            .setParseUpdates(
                Schema.SchemaParseUpdates.newBuilder()
                    .setFieldCreate(
                        Schema.SchemaParseUpdates.SchemaFieldCreate.newBuilder()
                            .setField(Schema.SchemaField.newBuilder().setJson(field).build())
                            .build())
                    .build())
            .build());
  }

  private List<Schema.SchemaUpdateData> updateExistingField(final Json.JsonFieldStats stats) {
    final var path = stats.getPath();
    final var name = stats.getName();
    final var oldType = schema.get(path, name).getType();
    schemaStats.put(ProtobufMapper.fromProtobuf(stats));
    final var newType = calculateType(schemaStats.get(path, name));
    if (oldType == newType) {
      return Collections.emptyList();
    }

    final var oldField = schema.get(path, name);
    final var newField = oldField.toBuilder().setType(newType).build();
    schema.put(newField);

    return Arrays.asList(
        Schema.SchemaUpdateData.newBuilder()
            .setFields(
                Schema.SchemaFields.newBuilder()
                    .setJson(Json.JsonSchema.newBuilder().addFields(newField).build()))
            .build(),
        Schema.SchemaUpdateData.newBuilder()
            .setParseUpdates(
                Schema.SchemaParseUpdates.newBuilder()
                    .setFieldUpdate(
                        Schema.SchemaParseUpdates.SchemaFieldUpdate.newBuilder()
                            .setPreviousField(
                                Schema.SchemaField.newBuilder().setJson(oldField).build())
                            .setNextField(Schema.SchemaField.newBuilder().setJson(newField).build())
                            .build())
                    .build())
            .build());
  }

  private List<Schema.SchemaUpdateData> processField(final Json.JsonFieldStats stats) {
    if (schema.containsKey(stats.getPath(), stats.getName())) {
      return updateExistingField(stats);
    } else {
      return createNewField(stats);
    }
  }

  @Override
  public void processElement(
      final Schema.SchemaEvent event, final Context ctx, final Collector<Schema.SchemaUpdate> out)
      throws Exception {
    if (!event.getData().hasFieldStats()
        || !event.getData().getFieldStats().getFieldStats().hasJson()) {
      return;
    }

    System.out.println("RECEIVED EVENT " + event);
    final var stats = event.getData().getFieldStats().getFieldStats().getJson();
    final var updates = processField(stats);
    for (final var update : updates) {
      out.collect(
          Schema.SchemaUpdate.newBuilder()
              .setMetadata(event.getMetadata())
              //.setData(update)
              .build());
    }
  }
}
