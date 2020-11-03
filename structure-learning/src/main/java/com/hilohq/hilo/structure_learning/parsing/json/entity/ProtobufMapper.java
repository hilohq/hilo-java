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
