package com.hilohq.hilo.features.stats.feature;

import java.util.HashMap;
import java.util.Map;

public final class DiscreteHistogram<T> {
  private final Map<T, Long> histogram;

  public DiscreteHistogram(final Map<T, Long> histogram) {
    this.histogram = histogram;
  }

  public DiscreteHistogram() {
    this(new HashMap<>());
  }

  public void observe(final T value, final long count) {
    if (!histogram.containsKey(value)) {
      histogram.put(value, 0L);
    }

    histogram.put(value, histogram.get(value) + count);
  }

  public void observe(final T value) {
    observe(value, 1);
  }

  public long count(final T value) {
    if (histogram.containsKey(value)) {
      return histogram.get(value);
    } else {
      return 0;
    }
  }
}
