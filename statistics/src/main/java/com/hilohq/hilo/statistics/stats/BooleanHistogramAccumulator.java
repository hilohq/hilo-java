package com.hilohq.hilo.statistics.stats;

import com.hilohq.hilo.protobuf.stats.Histogram;

public final class BooleanHistogramAccumulator
    implements StatsAccumulator<Boolean, Histogram.BooleanHistogram> {

  private long trueCount;
  private long falseCount;

  public BooleanHistogramAccumulator() {
    this(0, 0);
  }

  private BooleanHistogramAccumulator(final long trueCount, final long falseCount) {
    this.trueCount = trueCount;
    this.falseCount = falseCount;
  }

  @Override
  public void add(final Boolean value) {
    if (value) {
      ++trueCount;
    } else {
      ++falseCount;
    }
  }

  public static BooleanHistogramAccumulator fromSnapshot(
      final Histogram.BooleanHistogram histogram) {
    var trueCount = 0;
    var falseCount = 0;

    for (final var bucket : histogram.getBucketsList()) {
      if (bucket.getValue()) {
        trueCount += bucket.getCount();
      } else {
        falseCount += bucket.getCount();
      }
    }

    return new BooleanHistogramAccumulator(trueCount, falseCount);
  }

  @Override
  public Histogram.BooleanHistogram snapshot() {
    return Histogram.BooleanHistogram.newBuilder()
        .addBuckets(
            Histogram.BooleanHistogram.Bucket.newBuilder()
                .setCount(trueCount)
                .setValue(true)
                .build())
        .addBuckets(
            Histogram.BooleanHistogram.Bucket.newBuilder()
                .setCount(falseCount)
                .setValue(false)
                .build())
        .build();
  }
}
