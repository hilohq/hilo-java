package com.hilohq.hilo.features.stats.booleans;

import com.hilohq.hilo.features.stats.feature.FeatureStats;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;

public final class BooleanStats implements FeatureStats {
  private final BooleanHistogram histogram;

  public BooleanStats() {
    this(new BooleanHistogram());
  }

  private BooleanStats(final BooleanHistogram histogram) {
    this.histogram = histogram;
  }

  @Override
  public void observe(final Value value) {
    observe(value.booleanValue());
  }

  public void observe(final Boolean value) {
    this.histogram.observe(value);
  }

  public static BooleanStats fromSnapshot(final FeatureOuterClass.BooleanStats stats) {
    final var histogram = BooleanHistogram.fromSnapshot(stats.getHistogram());
    return new BooleanStats(histogram);
  }

  public static BooleanStats fromSnapshot(final FeatureOuterClass.FeatureStats stats) {
    if (!stats.hasBooleanStats()) {
      throw new IllegalArgumentException(
          "BooleanStatsAccumulator expects an instance of TypeStats " + "with BooleanStats set");
    }
    return fromSnapshot(stats.getBooleanStats());
  }

  public FeatureOuterClass.FeatureStats snapshot() {
    final var histogramSnapshot = histogram.snapshot();
    final var count =
        histogramSnapshot
            .getBucketsList()
            .stream()
            .mapToLong(FeatureOuterClass.BooleanStats.Histogram.Bucket::getCount)
            .sum();
    return FeatureOuterClass.FeatureStats.newBuilder()
        .setBooleanStats(
            FeatureOuterClass.BooleanStats.newBuilder()
                .setHistogram(histogramSnapshot)
                .setCount(count)
                .build())
        .build();
  }
}
