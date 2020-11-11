package com.hilohq.hilo.parsers.logparse.json;

import com.hilohq.hilo.parsers.logparse.log.TypeInfoFieldStats;
import com.hilohq.hilo.parsers.logparse.log.TypeInfoHistogram;
import com.hilohq.hilo.protobufs.structure.Json;

public final class JsonProtobufObjectMapper {

  public TypeInfoHistogram<JsonType> fromProtobuf(final Json.JsonTypeHistogram protobuf) {
    final var histogram = new TypeInfoHistogram<JsonType>();

    histogram.observe(JsonType.ARRAY, protobuf.getArrayCount());
    histogram.observe(JsonType.NULL, protobuf.getNullCount());
    histogram.observe(JsonType.OBJECT, protobuf.getObjectCount());
    histogram.observe(JsonType.NUMBER, protobuf.getNumberCount());
    histogram.observe(JsonType.BINARY, protobuf.getBinaryCount());
    histogram.observe(JsonType.BOOLEAN, protobuf.getBooleanCount());
    histogram.observe(JsonType.STRING, protobuf.getStringCount());

    return histogram;
  }

  public TypeInfoFieldStats<JsonType> fromProtobuf(final Json.JsonFieldStats protobuf) {
    return new TypeInfoFieldStats<>(fromProtobuf(protobuf.getTypeHistogram()));
  }

  public Json.JsonTypeHistogram toProtobuf(final TypeInfoHistogram<JsonType> histogram) {
    return Json.JsonTypeHistogram.newBuilder()
        .setArrayCount(histogram.count(JsonType.ARRAY))
        .setNullCount(histogram.count(JsonType.NULL))
        .setObjectCount(histogram.count(JsonType.OBJECT))
        .setNumberCount(histogram.count(JsonType.NUMBER))
        .setBinaryCount(histogram.count(JsonType.BINARY))
        .setBooleanCount(histogram.count(JsonType.BOOLEAN))
        .setStringCount(histogram.count(JsonType.STRING))
        .build();
  }

  public Json.JsonFieldStats toProtobuf(final TypeInfoFieldStats<JsonType> fieldStats) {
    return Json.JsonFieldStats.newBuilder()
        .setTypeHistogram(toProtobuf(fieldStats.typeInfoHistogram()))
        .build();
  }
}
