package com.hilohq.hilo.structure_learning.schema.json;

import static com.hilohq.hilo.parsers.logparse.json.JsonType.*;
import static com.hilohq.hilo.parsers.logparse.json.JsonUtils.fullname;

import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonProtobufObjectMapper;
import com.hilohq.hilo.parsers.logparse.json.JsonType;
import com.hilohq.hilo.parsers.logparse.log.TypeInfoFieldStats;
import com.hilohq.hilo.protobufs.structure.schema.Json;

public final class JsonProtobufUtils {

  private static final JsonProtobufObjectMapper mapper = new JsonProtobufObjectMapper();

  private JsonProtobufUtils() {}

  public static Json.JsonField createJsonField(
      final JsonFieldKey key, final JsonType type, final TypeInfoFieldStats<JsonType> stats) {
    return Json.JsonField.newBuilder()
        .setPath(key.path())
        .setName(key.name())
        .setKey(key.key())
        .setType(mapToProtoType(type))
        .setStats(mapper.toProtobuf(stats))
        .build();
  }

  public static Json.JsonEvent.FieldStats createJsonFieldStats(
      final String path, final String name, final TypeInfoFieldStats<JsonType> fieldStats) {
    return Json.JsonEvent.FieldStats.newBuilder()
        .setPath(path)
        .setName(name)
        .setKey(fullname(path, name))
        .setFieldStats(mapper.toProtobuf(fieldStats))
        .build();
  }

  public static Json.JsonEvent createJsonFieldStatsEvent(
      final String path, final String name, final TypeInfoFieldStats<JsonType> fieldStats) {
    return Json.JsonEvent.newBuilder()
        .setFieldStats(createJsonFieldStats(path, name, fieldStats))
        .build();
  }

  public static Json.JsonType mapToProtoType(final JsonType type) {
    switch (type) {
      case OBJECT:
        return Json.JsonType.JSON_OBJECT;
      case BOOLEAN:
        return Json.JsonType.JSON_BOOLEAN;
      case NUMBER:
        return Json.JsonType.JSON_NUMBER;
      case NULL:
        return Json.JsonType.JSON_NULL;
      case STRING:
        return Json.JsonType.JSON_STRING;
      case BINARY:
        return Json.JsonType.JSON_BINARY;
      case ARRAY:
        return Json.JsonType.JSON_ARRAY;
      default:
        throw new IllegalArgumentException("unknown JsonType " + type);
    }
  }

  public static JsonType mapFromProtoType(final Json.JsonType type) {
    switch (type) {
      case JSON_OBJECT:
        return OBJECT;
      case JSON_BOOLEAN:
        return BOOLEAN;
      case JSON_NUMBER:
        return NUMBER;
      case JSON_NULL:
        return NULL;
      case JSON_STRING:
        return STRING;
      case JSON_BINARY:
        return BINARY;
      case JSON_ARRAY:
        return ARRAY;
      default:
        throw new IllegalArgumentException("unknown JsonType " + type);
    }
  }

  public static Json.JsonLog createJsonFieldCreateLog(
      final String path, final String name, final JsonType type) {
    return Json.JsonLog.newBuilder()
        .setFieldCreate(
            Json.JsonFieldCreate.newBuilder()
                .setKey(fullname(path, name))
                .setName(name)
                .setPath(path)
                .setType(mapToProtoType(type)))
        .build();
  }

  public static Json.JsonLog createJsonFieldUpdateLog(
      final String path, final String name, final JsonType oldType, final JsonType newType) {
    return Json.JsonLog.newBuilder()
        .setFieldUpdate(
            Json.JsonFieldUpdate.newBuilder()
                .setBefore(
                    Json.JsonFieldUpdate.Field.newBuilder()
                        .setName(name)
                        .setPath(path)
                        .setKey(fullname(path, name))
                        .setType(mapToProtoType(oldType)))
                .setAfter(
                    Json.JsonFieldUpdate.Field.newBuilder()
                        .setName(name)
                        .setPath(path)
                        .setKey(fullname(path, name))
                        .setType(mapToProtoType(newType))))
        .build();
  }
}
