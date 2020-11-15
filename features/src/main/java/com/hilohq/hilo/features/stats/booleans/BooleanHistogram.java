package com.hilohq.hilo.features.stats.booleans;

import com.hilohq.hilo.features.stats.feature.DiscreteHistogram;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BooleanHistogram {

  private final DiscreteHistogram<Boolean> histogram;

  public BooleanHistogram() {
    this(new HashMap<>());
  }

  private BooleanHistogram(final Map<Boolean, Long> histogram) {
    this.histogram = new DiscreteHistogram<>(histogram);
  }

  public void observe(final Boolean value) {
    histogram.observe(value);
  }

  public void observe(final Boolean value, final long count) {
    histogram.observe(value, count);
  }

  public Set<Map.Entry<Boolean, Long>> entrySet() {
    return histogram.entrySet();
  }

  public static BooleanHistogram fromSnapshot(
      final FeatureOuterClass.BooleanStats.Histogram snapshot) {
    final var histogram = new BooleanHistogram();

    for (final var bucket : snapshot.getBucketsList()) {
      histogram.observe(bucket.getValue(), bucket.getCount());
    }

    return histogram;
  }

  public FeatureOuterClass.BooleanStats.Histogram snapshot() {
    return FeatureOuterClass.BooleanStats.Histogram.newBuilder()
        .addBuckets(
            FeatureOuterClass.BooleanStats.Histogram.Bucket.newBuilder()
                .setCount(histogram.count(true))
                .setValue(true)
                .build())
        .addBuckets(
            FeatureOuterClass.BooleanStats.Histogram.Bucket.newBuilder()
                .setCount(histogram.count(false))
                .setValue(false)
                .build())
        .build();
  }
}
