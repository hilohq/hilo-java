package com.hilohq.hilo.structure_learning.parsing.json;

import static com.hilohq.hilo.structure_learning.parsing.json.entity.JsonUtils.fullname;

import com.hilohq.hilo.protobufs.schema.Json;
import com.hilohq.hilo.protobufs.schema.Model;
import com.hilohq.hilo.protobufs.schema.Model.SchemaEvent;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonFieldStats;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonParseStats;
import com.hilohq.hilo.structure_learning.parsing.json.entity.JsonPropertyPath;
import com.hilohq.hilo.structure_learning.parsing.json.entity.ProtobufMapper;
import com.hilohq.hilo.structure_learning.parsing.schema.SchemaGenerate;
import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

public final class JsonSchemaGenerate extends SchemaGenerate<TextLineBatch> {
  private final ObjectMapper jsonMapper;
  private final Model.SchemaMetadata metadata;

  public JsonSchemaGenerate() {
    this(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
  }

  public JsonSchemaGenerate(final String projectId, final String sourceId, final String schemaId) {
    this.jsonMapper = new ObjectMapper();
    this.metadata =
        Model.SchemaMetadata.newBuilder()
            .setProjectId(projectId)
            .setSourceId(sourceId)
            .setSchemaId(schemaId)
            .build();
  }

  private static void updateNodeType(
      final String path,
      final String name,
      final Json.JsonType type,
      final Map<JsonPropertyPath, JsonFieldStats> schema) {
    final var key = new JsonPropertyPath(path, name);
    if (!schema.containsKey(key)) {
      schema.put(key, new JsonFieldStats());
    }

    schema.get(key).observe(type);
  }

  private static void updateSchemaValueNode(
      final String path,
      final String name,
      final JsonNode node,
      final Map<JsonPropertyPath, JsonFieldStats> schema) {
    if (!node.isValueNode()) {
      throw new IllegalArgumentException("expected value node");
    }

    if (node.isTextual()) {
      updateNodeType(path, name, Json.JsonType.JSON_STRING, schema);

    } else if (node.isIntegralNumber()) {
      updateNodeType(path, name, Json.JsonType.JSON_INTEGER, schema);

    } else if (node.isBoolean()) {
      updateNodeType(path, name, Json.JsonType.JSON_BOOLEAN, schema);

    } else if (node.isFloat()) {
      updateNodeType(path, name, Json.JsonType.JSON_FLOAT, schema);

    } else if (node.isNull()) {
      updateNodeType(path, name, Json.JsonType.JSON_NULL, schema);

    } else {
      throw new IllegalArgumentException("unexpected node type");
    }
  }

  private static void updateSchemaObjectNode(
      final String path,
      final String name,
      final JsonNode node,
      final Map<JsonPropertyPath, JsonFieldStats> schema) {
    if (!node.isObject()) {
      throw new IllegalArgumentException("expected object node");
    }

    final var fieldEntriesIt = node.fields();
    while (fieldEntriesIt.hasNext()) {
      final var fieldEntry = fieldEntriesIt.next();
      updateSchema(fullname(path, name), fieldEntry.getKey(), fieldEntry.getValue(), schema);
    }
  }

  private static void updateSchemaArrayNode(
      final String path,
      final String name,
      final JsonNode node,
      final Map<JsonPropertyPath, JsonFieldStats> schema) {
    updateNodeType(path, name, Json.JsonType.JSON_ARRAY, schema);
  }

  private static void updateSchema(
      final String path,
      final String name,
      final JsonNode node,
      final Map<JsonPropertyPath, JsonFieldStats> schema) {
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

  private static void updateSchema(
      final JsonNode node, final Map<JsonPropertyPath, JsonFieldStats> schema) {
    updateSchema("", "", node, schema);
  }

  private SchemaEvent fieldUpdateEvent(
      final String path, final String name, final JsonFieldStats field) {
    final var jsonStats =
        Json.JsonSchemaEvent.FieldStats.newBuilder()
            .setFieldStats(ProtobufMapper.toProtobuf(field))
            .setName(name)
            .setPath(path)
            .build();
    final var jsonEvent = Json.JsonSchemaEvent.newBuilder().setFieldStats(jsonStats).build();
    return SchemaEvent.newBuilder().setJson(jsonEvent).setMetadata(metadata).build();
  }

  private SchemaEvent parseErrorEvent(
      final String content, final String reason, final String description) {
    final var parseErrors =
        Json.JsonSchemaEvent.ParseErrors.newBuilder()
            .addParseErrors(
                Json.JsonParseError.newBuilder()
                    .setContent(content)
                    .setReason(reason)
                    .setDescription(description)
                    .build())
            .build();
    final var jsonEvent = Json.JsonSchemaEvent.newBuilder().setParseErrors(parseErrors).build();
    return SchemaEvent.newBuilder().setJson(jsonEvent).setMetadata(metadata).build();
  }

  private SchemaEvent parseStatsEvent(final JsonParseStats stats) {
    final var parseStats =
        Json.JsonSchemaEvent.ParseStats.newBuilder()
            .setStats(
                Json.JsonParseStats.newBuilder()
                    .setParseAttemptCount(stats.parseAttemptCount())
                    .setParseErrorCount(stats.parseErrorCount())
                    .setParseSuccessCount(stats.parseSuccessCount())
                    .build())
            .build();
    final var jsonEvent = Json.JsonSchemaEvent.newBuilder().setParseStats(parseStats).build();
    return SchemaEvent.newBuilder().setJson(jsonEvent).setMetadata(metadata).build();
  }

  @Override
  public void processElement(
      final TextLineBatch batch, final Context ctx, final Collector<SchemaEvent> out)
      throws Exception {
    final JsonParseStats parseStats = new JsonParseStats();
    final var schemaStats = new HashMap<JsonPropertyPath, JsonFieldStats>();
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

    for (final var fieldEntry : schemaStats.entrySet()) {
      final var event =
          fieldUpdateEvent(
              fieldEntry.getKey().path(), fieldEntry.getKey().name(), fieldEntry.getValue());
      out.collect(event);
    }

    out.collect(parseStatsEvent(parseStats));
  }
}
