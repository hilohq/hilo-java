package com.hilohq.hilo.structure_learning.schema.json;

import static com.hilohq.hilo.structure_learning.schema.json.JsonProtobufUtils.*;

import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonProtobufObjectMapper;
import com.hilohq.hilo.parsers.logparse.json.JsonType;
import com.hilohq.hilo.parsers.logparse.log.TypeInfoFieldStats;
import com.hilohq.hilo.parsers.logparse.log.TypeInfoPrefer;
import com.hilohq.hilo.protobufs.structure.Json;
import com.hilohq.hilo.protobufs.structure.Schema;
import java.util.*;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

public final class JsonSchemaStatsAggregate
    extends RichGroupReduceFunction<Schema.SchemaEvent, Schema.SchemaUpdate> {
  private Collection<Json.JsonLog> generateLogs(
      final Map<String, Json.JsonField> current, final JsonFieldKey key, final JsonType newType) {
    final var result = new ArrayList<Json.JsonLog>();

    if (!current.containsKey(key.key())) {
      result.add(createJsonFieldCreateLog(key.path(), key.name(), newType));

    } else if (!current.get(key.key()).getType().equals(mapToProtoType(newType))) {
      result.add(
          createJsonFieldUpdateLog(
              key.path(), key.name(), mapFromProtoType(current.get(key.key()).getType()), newType));
    }

    return result;
  }

  @Override
  public void reduce(
      final Iterable<Schema.SchemaEvent> events, final Collector<Schema.SchemaUpdate> out)
      throws Exception {
    final var mapper = new JsonProtobufObjectMapper();
    final var prefer = new TypeInfoPrefer<JsonType>();
    final var current = new HashMap<String, Json.JsonField>();
    final var stats = new HashMap<String, TypeInfoFieldStats<JsonType>>();
    final var keys = new HashMap<String, JsonFieldKey>();
    Schema.SchemaMetadata metadata = null;

    for (final var event : events) {
      metadata = event.getMetadata();

      if (!event.hasJson() || !event.getJson().hasFieldStats()) {
        continue;
      }

      final var value = event.getJson().getFieldStats();
      final var fieldStats = mapper.fromProtobuf(value.getFieldStats());
      if (!stats.containsKey(value.getKey())) {
        stats.put(value.getKey(), new TypeInfoFieldStats<>());
      }

      stats.put(value.getKey(), stats.get(value.getKey()).merge(fieldStats));
      keys.put(value.getKey(), new JsonFieldKey(value.getPath(), value.getName()));
    }

    for (final var entry : stats.entrySet()) {
      final var type = prefer.fromStats(entry.getValue(), JsonType.NULL);
      final var key = keys.get(entry.getKey());
      final var logs = generateLogs(current, key, type);
      for (final var log : logs) {
        out.collect(
            Schema.SchemaUpdate.newBuilder()
                .setMetadata(metadata)
                .setJson(Json.JsonUpdate.newBuilder().setLog(log))
                .build());
      }

      out.collect(
          Schema.SchemaUpdate.newBuilder()
              .setMetadata(metadata)
              .setJson(
                  Json.JsonUpdate.newBuilder()
                      .setField(createJsonField(key, type, entry.getValue())))
              .build());
    }
  }
}
