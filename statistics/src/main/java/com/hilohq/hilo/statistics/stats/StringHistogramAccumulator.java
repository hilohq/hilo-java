package com.hilohq.hilo.statistics.stats;

import com.hilohq.hilo.protobuf.stats.Histogram;
import java.util.HashMap;
import java.util.Map;

public final class StringHistogramAccumulator
    implements StatsAccumulator<String, Histogram.StringHistogram> {
  private final Map<String, Long> histogram;

  private StringHistogramAccumulator(final Map<String, Long> histogram) {
    this.histogram = histogram;
  }

  public StringHistogramAccumulator() {
    this(new HashMap<>());
  }

  public void add(final String value) {
    if (!histogram.containsKey(value)) {
      histogram.put(value, 0L);
    }

    histogram.put(value, histogram.get(value) + 1);
  }

  public static StringHistogramAccumulator fromSnapshot(final Histogram.StringHistogram histogram) {
    final var buckets = new HashMap<String, Long>();
    for (final var bucket : histogram.getBucketsList()) {
      buckets.put(bucket.getValue(), bucket.getCount());
    }
    return new StringHistogramAccumulator(buckets);
  }

  @Override
  public Histogram.StringHistogram snapshot() {
    final var histogramBuilder = Histogram.StringHistogram.newBuilder();

    for (final var entry : histogram.entrySet()) {
      final var bucket =
          Histogram.StringHistogram.Bucket.newBuilder()
              .setCount(entry.getValue())
              .setValue(entry.getKey());
      histogramBuilder.addBuckets(bucket);
    }
    return histogramBuilder.build();
  }
}
