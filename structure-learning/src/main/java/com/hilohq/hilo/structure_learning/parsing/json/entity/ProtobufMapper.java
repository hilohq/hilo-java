package com.hilohq.hilo.structure_learning.parsing.json.entity;

import com.hilohq.hilo.protobufs.schema.Json;

public final class ProtobufMapper {

  private ProtobufMapper() {}

  public static Json.JsonFieldStats toProtobuf(final JsonFieldStats field) {
    return Json.JsonFieldStats.newBuilder()
        .setTypeHistogram(toProtobuf(field.typeHistogram()))
        .build();
  }

  public static JsonFieldStats fromProtobuf(final Json.JsonFieldStats field) {
    return new JsonFieldStats(fromProtobuf(field.getTypeHistogram()));
  }

  public static Json.JsonTypeHistogram toProtobuf(final JsonTypeHistogram histogram) {
    return Json.JsonTypeHistogram.newBuilder()
        .setArrayCount(histogram.counts(Json.JsonType.JSON_ARRAY))
        .setBooleanCount(histogram.counts(Json.JsonType.JSON_BOOLEAN))
        .setIntegerCount(histogram.counts(Json.JsonType.JSON_INTEGER))
        .setFloatCount(histogram.counts(Json.JsonType.JSON_FLOAT))
        .setStringCount(histogram.counts(Json.JsonType.JSON_STRING))
        .setNullCount(histogram.counts(Json.JsonType.JSON_NULL))
        .setObjectCount(histogram.counts(Json.JsonType.JSON_OBJECT))
        .build();
  }

  public static JsonTypeHistogram fromProtobuf(final Json.JsonTypeHistogram histogram) {
    final JsonTypeHistogram typeHistogram = new JsonTypeHistogram();

    typeHistogram.observe(Json.JsonType.JSON_ARRAY, histogram.getArrayCount());
    typeHistogram.observe(Json.JsonType.JSON_BOOLEAN, histogram.getBooleanCount());
    typeHistogram.observe(Json.JsonType.JSON_INTEGER, histogram.getIntegerCount());
    typeHistogram.observe(Json.JsonType.JSON_FLOAT, histogram.getFloatCount());
    typeHistogram.observe(Json.JsonType.JSON_STRING, histogram.getStringCount());
    typeHistogram.observe(Json.JsonType.JSON_NULL, histogram.getNullCount());
    typeHistogram.observe(Json.JsonType.JSON_OBJECT, histogram.getObjectCount());

    return typeHistogram;
  }
}
