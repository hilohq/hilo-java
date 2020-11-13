package com.hilohq.hilo.features.stats.strings;

import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;
import java.util.HashMap;
import java.util.Map;

public final class StringHistogram {
  private final Map<String, Long> histogram;

  private StringHistogram(final Map<String, Long> histogram) {
    this.histogram = histogram;
  }

  public StringHistogram() {
    this(new HashMap<>());
  }

  public void add(final String value) {
    if (!histogram.containsKey(value)) {
      histogram.put(value, 0L);
    }

    histogram.put(value, histogram.get(value) + 1);
  }

  public static StringHistogram fromSnapshot(
      final FeatureOuterClass.StringStats.Histogram histogram) {
    final var buckets = new HashMap<String, Long>();
    for (final var bucket : histogram.getBucketsList()) {
      buckets.put(bucket.getValue(), bucket.getCount());
    }
    return new StringHistogram(buckets);
  }

  public FeatureOuterClass.StringStats.Histogram snapshot() {
    final var histogramBuilder = FeatureOuterClass.StringStats.Histogram.newBuilder();

    for (final var entry : histogram.entrySet()) {
      final var bucket =
          FeatureOuterClass.StringStats.Histogram.Bucket.newBuilder()
              .setCount(entry.getValue())
              .setValue(entry.getKey());
      histogramBuilder.addBuckets(bucket);
    }
    return histogramBuilder.build();
  }
}
