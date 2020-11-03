package com.hilohq.hilo.structure_learning.parsing.json;

import static com.hilohq.hilo.protobufs.parsing.Schema.*;
import static com.hilohq.hilo.structure_learning.parsing.json.entity.JsonUtils.fieldFullName;

import com.hilohq.hilo.protobufs.parsing.Json;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonFieldStats;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonParseStats;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonSchemaStats;
import com.hilohq.hilo.structure_learning.parsing.json.entity.ProtobufMapper;
import com.hilohq.hilo.structure_learning.parsing.schema.SchemaGenerate;
import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import java.util.UUID;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

public final class JsonSchemaGenerate extends SchemaGenerate<TextLineBatch> {
  private final String projectId;
  private final String sourceId;
  private final String schemaId;
  private final ObjectMapper jsonMapper;

  public JsonSchemaGenerate() {
    this(UUID.randomUUID().toString(), UUID.randomUUID().toString());
  }

  public JsonSchemaGenerate(final String projectId, final String sourceId) {
    this.projectId = projectId;
    this.sourceId = sourceId;
    this.schemaId = UUID.randomUUID().toString();
    this.jsonMapper = new ObjectMapper();
  }

  private static void updateNodeType(
      final String path,
      final String name,
      final Json.JsonType type,
      final JsonSchemaStats schema) {
    schema.put(path, name, type);
  }

  private static void updateSchemaValueNode(
      final String path, final String name, final JsonNode node, final JsonSchemaStats schema) {
    if (!node.isValueNode()) {
      throw new IllegalArgumentException("expected value node");
    }

    if (node.isTextual()) {
      updateNodeType(path, name, Json.JsonType.STRING, schema);

    } else if (node.isIntegralNumber()) {
      updateNodeType(path, name, Json.JsonType.INTEGER, schema);

    } else if (node.isBoolean()) {
      updateNodeType(path, name, Json.JsonType.BOOLEAN, schema);

    } else if (node.isFloat()) {
      updateNodeType(path, name, Json.JsonType.FLOAT, schema);

    } else if (node.isNull()) {
      updateNodeType(path, name, Json.JsonType.NULL, schema);

    } else {
      throw new IllegalArgumentException("unexpected node type");
    }
  }

  private static void updateSchemaObjectNode(
      final String path, final String name, final JsonNode node, final JsonSchemaStats schema) {
    if (!node.isObject()) {
      throw new IllegalArgumentException("expected object node");
    }

    final var fieldEntriesIt = node.fields();
    while (fieldEntriesIt.hasNext()) {
      final var fieldEntry = fieldEntriesIt.next();
      updateSchema(fieldFullName(path, name), fieldEntry.getKey(), fieldEntry.getValue(), schema);
    }
  }

  private static void updateSchemaArrayNode(
      final String path, final String name, final JsonNode node, final JsonSchemaStats schema) {
    updateNodeType(path, name, Json.JsonType.ARRAY, schema);
  }

  private static void updateSchema(
      final String path, final String name, final JsonNode node, final JsonSchemaStats schema) {
    if (!node.isContainerNode()) {
      updateSchemaValueNode(path, name, node, schema);

    } else if (node.isObject()) {
      updateSchemaObjectNode(path, name, node, schema);

    } else if (node.isArray()) {
      updateSchemaArrayNode(path, name, node, schema);

    } else {
      throw new IllegalArgumentException("unexpected node type");
    }
  }

  private static void updateSchema(final JsonNode node, final JsonSchemaStats schema) {
    updateSchema("", "", node, schema);
  }

  private SchemaEvent fieldUpdateEvent(final JsonFieldStats field) {
    final var fieldBuilder =
        SchemaFieldStats.newBuilder().setJson(ProtobufMapper.toProtobuf(field));
    final var statsBuilder =
        SchemaEventData.SchemaEventFieldStats.newBuilder().setFieldStats(fieldBuilder);
    return SchemaEvent.newBuilder()
        .setData(SchemaEventData.newBuilder().setFieldStats(statsBuilder).build())
        .setMetadata(
            SchemaMetadata.newBuilder()
                .setProjectId(projectId)
                .setSchemaId(schemaId)
                .setSourceId(sourceId)
                .build())
        .build();
  }

  private SchemaEvent parseErrorEvent(
      final String content, final String reason, final String description) {
    final var errorsBuilder =
        SchemaEventData.SchemaEventParseErrors.newBuilder()
            .addParseErrors(
                SchemaParseError.newBuilder()
                    .setContent(content)
                    .setReason(reason)
                    .setDescription(description)
                    .build())
            .build();

    return SchemaEvent.newBuilder()
        .setData(SchemaEventData.newBuilder().setParseErrors(errorsBuilder).build())
        .setMetadata(
            SchemaMetadata.newBuilder()
                .setProjectId(projectId)
                .setSchemaId(schemaId)
                .setSourceId(sourceId)
                .build())
        .build();
  }

  @Override
  public void processElement(
      final TextLineBatch batch, final Context ctx, final Collector<SchemaEvent> out)
      throws Exception {
    final JsonParseStats parseStats = new JsonParseStats();
    final JsonSchemaStats schemaStats = new JsonSchemaStats();
    for (final var line : batch.lines()) {
      try {
        parseStats.incrParseAttemptCount();
        final var node = jsonMapper.readTree(line);
        updateSchema(node, schemaStats);
        parseStats.incrParseSuccessCount();

      } catch (JsonProcessingException e) {
        parseStats.incrParseErrorCount();
        out.collect(parseErrorEvent(line, "Invalid JSON", e.getMessage()));
      }
    }

    for (final var field : schemaStats.fields().values()) {
      out.collect(fieldUpdateEvent(field));
    }

    out.collect(
        SchemaEvent.newBuilder()
            .setData(
                SchemaEventData.newBuilder()
                    .setParseStats(
                        SchemaEventData.SchemaEventParseStats.newBuilder()
                            .setParseAttemptCount(parseStats.parseAttemptCount())
                            .setParseErrorCount(parseStats.parseErrorCount())
                            .setParseSuccessCount(parseStats.parseSuccessCount())
                            .build()))
            .setMetadata(
                SchemaMetadata.newBuilder()
                    .setProjectId(projectId)
                    .setSchemaId(schemaId)
                    .setSourceId(sourceId)
                    .build())
            .build());
  }
}
