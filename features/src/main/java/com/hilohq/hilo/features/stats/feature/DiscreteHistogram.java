package com.hilohq.hilo.features.stats.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Counts occurrences of discrete values. Tracking stops once more than {@value #maxItems} distinct
 * values have been seen; from then on the histogram is empty and {@link #keepsTrack()} is false.
 */
public final class DiscreteHistogram<T> {
  private static final int maxItems = 1024;
  private final Map<T, Long> histogram;
  private boolean keepsTrack;

  public DiscreteHistogram(final boolean keepsTrack, final Map<T, Long> histogram) {
    this.keepsTrack = keepsTrack;
    this.histogram = histogram;
    if (!keepsTrack) {
      histogram.clear();
    }
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
      histogram.clear();
    }
  }

  /**
   * Adds the counts of {@code other} to this histogram. If either side has stopped tracking, the
   * result does not track either, since its counts would be incomplete.
   */
  public void merge(final DiscreteHistogram<T> other) {
    if (!other.keepsTrack) {
      keepsTrack = false;
      histogram.clear();
      return;
    }

    for (final var entry : other.histogram.entrySet()) {
      observe(entry.getKey(), entry.getValue());
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
