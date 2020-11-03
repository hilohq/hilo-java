package com.hilohq.hilo.structure_learning.parsing.json.entity;

import com.hilohq.hilo.protobufs.schema.Json.JsonType;

public final class JsonFieldStats {

  private final JsonTypeHistogram typeHistogram;

  public JsonFieldStats() {
    this(new JsonTypeHistogram());
  }

  public JsonFieldStats(final JsonTypeHistogram typeHistogram) {
    this.typeHistogram = typeHistogram;
  }

  public JsonTypeHistogram typeHistogram() {
    return typeHistogram;
  }

  public void observe(final JsonType type) {
    typeHistogram.observe(type);
  }

  public void mergeFrom(final JsonFieldStats stats) {
    for (final var entry : stats.typeHistogram().counts().entrySet()) {
      typeHistogram.observe(entry.getKey(), entry.getValue());
    }
  }
}
