package com.hilohq.hilo.features.stats.strings;

import com.hilohq.hilo.features.stats.feature.DiscreteHistogram;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class StringHistogram {
  private final DiscreteHistogram<String> histogram;

  private StringHistogram(final boolean keepsTrack, final Map<String, Long> histogram) {
    this.histogram = new DiscreteHistogram<>(keepsTrack, histogram);
  }

  public StringHistogram() {
    this(true, new HashMap<>());
  }

  public void observe(final String value, final long count) {
    histogram.observe(value, count);
  }

  public void observe(final String value) {
    observe(value, 1);
  }

  public Set<Map.Entry<String, Long>> entrySet() {
    return histogram.entrySet();
  }

  public static StringHistogram fromSnapshot(
      final FeatureOuterClass.StringStats.Histogram histogram) {
    final var buckets = new HashMap<String, Long>();
    for (final var bucket : histogram.getBucketsList()) {
      buckets.put(bucket.getValue(), bucket.getCount());
    }
    return new StringHistogram(histogram.getKeepsTrack(), buckets);
  }

  public FeatureOuterClass.StringStats.Histogram snapshot() {
    final var histogramBuilder = FeatureOuterClass.StringStats.Histogram.newBuilder()
            .setKeepsTrack(histogram.keepsTrack());

    if (histogram.keepsTrack()) {
      for (final var entry : histogram.entrySet()) {
        final var bucket =
                FeatureOuterClass.StringStats.Histogram.Bucket.newBuilder()
                        .setCount(entry.getValue())
                        .setValue(entry.getKey());
        histogramBuilder.addBuckets(bucket);
      }
    }

    return histogramBuilder.build();
  }
}
