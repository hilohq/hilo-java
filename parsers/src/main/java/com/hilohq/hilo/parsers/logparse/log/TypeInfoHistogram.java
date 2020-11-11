package com.hilohq.hilo.parsers.logparse.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TypeInfoHistogram<T> {
  private final Map<T, Long> histogram;

  public TypeInfoHistogram() {
    this.histogram = new HashMap<>();
  }

  public long count(final T type) {
    if (histogram.containsKey(type)) {
      return histogram.get(type);
    } else {
      return 0;
    }
  }

  public Set<Map.Entry<T, Long>> entrySet() {
    return histogram.entrySet();
  }

  public void observe(final T type, final long count) {
    if (!histogram.containsKey(type)) {
      histogram.put(type, 0L);
    }
    histogram.put(type, histogram.get(type) + count);
  }

  public void observe(final T type) {
    observe(type, 1);
  }
}
