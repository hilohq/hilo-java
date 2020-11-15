package com.hilohq.hilo.features.stats.booleans;

import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;

public final class BooleanStats
    implements StatsCollector, Snapshot<FeatureOuterClass.BooleanStats> {
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

  @Override
  public void merge(final StatsCollector collector) {
    if (!collector.getClass().equals(BooleanStats.class)) {
      throw new IllegalArgumentException(
          "BooleanStats expects another " + "instance of BooleanStats for a merge");
    }

    final var other = (BooleanStats) collector;
    for (final var entry : other.histogram.entrySet()) {
      histogram.observe(entry.getKey(), entry.getValue());
    }
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

  public FeatureOuterClass.BooleanStats snapshot() {
    final var histogramSnapshot = histogram.snapshot();
    final var count =
        histogramSnapshot
            .getBucketsList()
            .stream()
            .mapToLong(FeatureOuterClass.BooleanStats.Histogram.Bucket::getCount)
            .sum();
    return FeatureOuterClass.BooleanStats.newBuilder()
        .setHistogram(histogramSnapshot)
        .setCount(count)
        .build();
  }
}
