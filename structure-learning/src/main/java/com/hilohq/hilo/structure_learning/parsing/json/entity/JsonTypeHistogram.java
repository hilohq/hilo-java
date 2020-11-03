package com.hilohq.hilo.structure_learning.parsing.json.entity;

import com.hilohq.hilo.protobufs.parsing.Json;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonTypeHistogram {

  private final Map<Json.JsonType, Long> histogram;

  private JsonTypeHistogram(final Map<Json.JsonType, Long> histogram) {
    this.histogram = histogram;
  }

  private static Map<Json.JsonType, Long> initializeMap() {
    final var histogram = new HashMap<Json.JsonType, Long>();
    for (final var value : Json.JsonType.values()) {
      histogram.put(value, 0L);
    }
    return histogram;
  }

  public JsonTypeHistogram() {
    this(initializeMap());
  }

  public void observe(final Json.JsonType type) {
    observe(type, 1);
  }

  public void observe(final Json.JsonType type, final long count) {
    histogram.put(type, histogram.get(type) + count);
  }

  public Map<Json.JsonType, Long> counts() {
    return Collections.unmodifiableMap(histogram);
  }

  public long counts(final Json.JsonType type) {
    return histogram.get(type);
  }
}
