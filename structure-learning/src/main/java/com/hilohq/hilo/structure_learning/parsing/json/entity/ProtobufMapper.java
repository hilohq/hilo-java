package com.hilohq.hilo.structure_learning.parsing.json.entity;

import com.hilohq.hilo.protobufs.parsing.Json;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchema;

public final class ProtobufMapper {

  private ProtobufMapper() {}

  public static Json.JsonSchema toProtobuf(final JsonSchema schema) {
    final var builder = Json.JsonSchema.newBuilder();
    for (final var field : schema.fields()) {
      builder.addFields(field);
    }
    return builder.build();
  }

  public static JsonSchema fromProtobuf(final Json.JsonSchema schema) {
    final JsonSchema result = new JsonSchema();
    for (final var field : schema.getFieldsList()) {
      result.put(field);
    }
    return result;
  }

  public static Json.JsonFieldStats toProtobuf(final JsonFieldStats field) {
    return Json.JsonFieldStats.newBuilder()
        .setName(field.name())
        .setPath(field.path())
        .setTypeHistogram(toProtobuf(field.typeHistogram()))
        .build();
  }

  public static JsonFieldStats fromProtobuf(final Json.JsonFieldStats field) {
    return new JsonFieldStats(
        field.getPath(), field.getName(), fromProtobuf(field.getTypeHistogram()));
  }

  public static Json.JsonTypeHistogram toProtobuf(final JsonTypeHistogram histogram) {
    return Json.JsonTypeHistogram.newBuilder()
        .setArrayCount(histogram.counts(Json.JsonType.ARRAY))
        .setBooleanCount(histogram.counts(Json.JsonType.BOOLEAN))
        .setIntegerCount(histogram.counts(Json.JsonType.INTEGER))
        .setFloatCount(histogram.counts(Json.JsonType.FLOAT))
        .setStringCount(histogram.counts(Json.JsonType.STRING))
        .setNullCount(histogram.counts(Json.JsonType.NULL))
        .setObjectCount(histogram.counts(Json.JsonType.OBJECT))
        .build();
  }

  public static JsonTypeHistogram fromProtobuf(final Json.JsonTypeHistogram histogram) {
    final JsonTypeHistogram typeHistogram = new JsonTypeHistogram();

    typeHistogram.observe(Json.JsonType.ARRAY, histogram.getArrayCount());
    typeHistogram.observe(Json.JsonType.BOOLEAN, histogram.getBooleanCount());
    typeHistogram.observe(Json.JsonType.INTEGER, histogram.getIntegerCount());
    typeHistogram.observe(Json.JsonType.FLOAT, histogram.getFloatCount());
    typeHistogram.observe(Json.JsonType.STRING, histogram.getStringCount());
    typeHistogram.observe(Json.JsonType.NULL, histogram.getNullCount());
    typeHistogram.observe(Json.JsonType.OBJECT, histogram.getObjectCount());

    return typeHistogram;
  }
}
