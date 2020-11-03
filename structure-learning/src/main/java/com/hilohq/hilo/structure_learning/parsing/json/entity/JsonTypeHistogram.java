package com.hilohq.hilo.structure_learning.parsing.json.entity;

import static com.hilohq.hilo.protobufs.schema.Json.JsonType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonTypeHistogram {

  private final Map<JsonType, Long> histogram;

  private JsonTypeHistogram(final Map<JsonType, Long> histogram) {
    this.histogram = histogram;
  }

  private static Map<JsonType, Long> initializeMap() {
    final var histogram = new HashMap<JsonType, Long>();
    for (final var value : JsonType.values()) {
      histogram.put(value, 0L);
    }
    return histogram;
  }

  public JsonTypeHistogram() {
    this(initializeMap());
  }

  public void observe(final JsonType type) {
    observe(type, 1);
  }

  public void observe(final JsonType type, final long count) {
    histogram.put(type, histogram.get(type) + count);
  }

  public Map<JsonType, Long> counts() {
    return Collections.unmodifiableMap(histogram);
  }

  public long counts(final JsonType type) {
    return histogram.get(type);
  }
}
