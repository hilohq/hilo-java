package com.hilohq.hilo.structure_learning.parsing.json.entity;

import com.hilohq.hilo.protobufs.parsing.Json;
import org.apache.commons.lang3.StringUtils;

public final class JsonFieldStats {

  private final String path;
  private final String name;
  private final JsonTypeHistogram typeHistogram;

  public JsonFieldStats(final String path, final String name) {
    this(path, name, new JsonTypeHistogram());
  }

  public JsonFieldStats(
      final String path, final String name, final JsonTypeHistogram typeHistogram) {
    this.path = path;
    this.name = name;
    this.typeHistogram = typeHistogram;
  }

  public String fullName() {
    return JsonUtils.fieldFullName(path, name);
  }

  public String path() {
    return path;
  }

  public String name() {
    return name;
  }

  public JsonTypeHistogram typeHistogram() {
    return typeHistogram;
  }

  public void observe(final Json.JsonType type) {
    typeHistogram.observe(type);
  }

  public void mergeFrom(final JsonFieldStats stats) {
    if (!StringUtils.equals(stats.path(), path) || !StringUtils.equals(stats.name(), name)) {
      throw new IllegalArgumentException("attempt to merge stats for different fields");
    }

    for (final var entry : stats.typeHistogram().counts().entrySet()) {
      typeHistogram.observe(entry.getKey(), entry.getValue());
    }
  }
}
