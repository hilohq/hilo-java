package com.hilohq.hilo.features.stats.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class DiscreteHistogram<T> {
  private static final int maxItems = 1024;
  private final Map<T, Long> histogram;
  private boolean keepsTrack;

  public DiscreteHistogram(final boolean keepsTrack, final Map<T, Long> histogram) {
    this.histogram = histogram;
  }

  public DiscreteHistogram() {
    this(true, new HashMap<>());
  }

  public void observe(final T value, final long count) {
    if (!keepsTrack) {
      return;
    }

    if (!histogram.containsKey(value)) {
      histogram.put(value, 0L);
    }

    histogram.put(value, histogram.get(value) + count);
    if (histogram.size() > maxItems) {
      keepsTrack = false;
    }
  }

  public Set<Map.Entry<T, Long>> entrySet() {
    return histogram.entrySet();
  }

  public void observe(final T value) {
    observe(value, 1);
  }

  public int size() {
    return histogram.size();
  }

  public boolean keepsTrack() {
    return keepsTrack;
  }

  public long count(final T value) {
    if (histogram.containsKey(value)) {
      return histogram.get(value);
    } else {
      return 0;
    }
  }
}
