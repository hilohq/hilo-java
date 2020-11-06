package com.hilohq.hilo.structure_learning.parsing.json;

import static com.hilohq.hilo.structure_learning.parsing.json.entity.JsonUtils.fullname;

import com.hilohq.hilo.protobufs.schema.Json;
import com.hilohq.hilo.protobufs.schema.Json.JsonSchemaEvent;
import com.hilohq.hilo.protobufs.schema.Model;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonFieldStats;
import com.hilohq.hilo.structure_learning.parsing.json.entity.ProtobufMapper;
import java.util.*;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public final class JsonSchemaFieldsAggregator
    extends ProcessFunction<Model.SchemaEvent, Model.SchemaUpdate> {

  private final Map<String, Json.JsonFieldModel> fields;

  public JsonSchemaFieldsAggregator() {
    this.fields = new HashMap<>();
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

  private List<Json.JsonSchemaUpdate> createNewField(final JsonSchemaEvent.FieldStats stats) {
    final var path = stats.getPath();
    final var name = stats.getName();
    final var key = fullname(path, name);
    final var type = calculateType(ProtobufMapper.fromProtobuf(stats.getFieldStats()));
    final var field = Json.JsonField.newBuilder().setPath(path).setName(name).setType(type).build();
    fields.put(
        key,
        Json.JsonFieldModel.newBuilder().setStats(stats.getFieldStats()).setField(field).build());

    final var fieldModel =
        Json.JsonFieldModel.newBuilder().setField(field).setStats(stats.getFieldStats()).build();

    return Arrays.asList(
        Json.JsonSchemaUpdate.newBuilder()
            .setFieldLogs(
                Json.JsonSchemaUpdate.FieldLogs.newBuilder()
                    .setFieldLogs(
                        Json.JsonLogsModel.newBuilder()
                            .setFieldCreate(
                                Json.JsonFieldCreateModel.newBuilder().setField(field).build())
                            .build())
                    .build())
            .build(),
        Json.JsonSchemaUpdate.newBuilder()
            .setSchemaUpdate(
                Json.JsonSchemaUpdate.SchemaUpdate.newBuilder().addFields(fieldModel).build())
            .build());
  }

  private List<Json.JsonSchemaUpdate> updateExistingField(final JsonSchemaEvent.FieldStats stats) {
    final var path = stats.getPath();
    final var name = stats.getName();
    final var key = fullname(path, name);
    final var oldField = fields.get(key);
    final var newType = calculateType(ProtobufMapper.fromProtobuf(stats.getFieldStats()));
    final var updates = new ArrayList<Json.JsonSchemaUpdate>();
    final var newField =
        oldField.toBuilder().setField(oldField.getField().toBuilder().setType(newType)).build();
    fields.put(key, newField);

    if (oldField.getField().getType() != newType) {
      updates.add(
          Json.JsonSchemaUpdate.newBuilder()
              .setFieldLogs(
                  Json.JsonSchemaUpdate.FieldLogs.newBuilder()
                      .setFieldLogs(
                          Json.JsonLogsModel.newBuilder()
                              .setFieldUpdate(
                                  Json.JsonFieldUpdateModel.newBuilder()
                                      .setFieldOld(oldField.getField())
                                      .setFieldNew(newField.getField())
                                      .build())
                              .build())
                      .build())
              .build());
    }

    final var update =
        Json.JsonSchemaUpdate.newBuilder()
            .setSchemaUpdate(
                Json.JsonSchemaUpdate.SchemaUpdate.newBuilder().addFields(newField).build())
            .build();
    updates.add(update);
    return updates;
  }

  private List<Json.JsonSchemaUpdate> processField(final JsonSchemaEvent.FieldStats stats) {
    final var key = fullname(stats.getPath(), stats.getName());
    if (fields.containsKey(key)) {
      return updateExistingField(stats);
    } else {
      return createNewField(stats);
    }
  }

  private List<Json.JsonSchemaUpdate> processEvent(final JsonSchemaEvent event) {
    if (event.hasFieldStats()) {
      final var stats = event.getFieldStats();
      return processField(stats);

    } else if (event.hasParseErrors()) {
      final var errors =
          Json.JsonParseErrorsModel.newBuilder()
              .addAllParseErrors(event.getParseErrors().getParseErrorsList())
              .build();
      return Collections.singletonList(
          Json.JsonSchemaUpdate.newBuilder()
              .setParseErrors(
                  Json.JsonSchemaUpdate.ParseErrors.newBuilder().setParseErrors(errors).build())
              .build());

    } else if (event.hasParseStats()) {
      final var stats =
          Json.JsonSchemaUpdate.SchemaUpdate.newBuilder()
              .setStats(event.getParseStats().getStats())
              .build();
      return Collections.singletonList(
          Json.JsonSchemaUpdate.newBuilder().setSchemaUpdate(stats).build());

    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public void processElement(
      final Model.SchemaEvent event, final Context ctx, final Collector<Model.SchemaUpdate> out)
      throws Exception {
    if (!event.hasJson()) {
      return;
    }

    final var updates = processEvent(event.getJson());
    for (final var update : updates) {
      System.out.println("SERIALIZE " + update);
      out.collect(
          Model.SchemaUpdate.newBuilder().setMetadata(event.getMetadata()).setJson(update).build());
    }
  }
}
