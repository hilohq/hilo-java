package com.hilohq.hilo.statistics.stats;

import com.hilohq.hilo.protobuf.stats.Histogram;
import com.hilohq.hilo.protobuf.stats.Stats;

public final class BooleanStatsAccumulator
    implements StatsAccumulator<Boolean, Stats.BooleanStats> {
  private final BooleanHistogramAccumulator histogramAccumulator;

  public BooleanStatsAccumulator() {
    this(new BooleanHistogramAccumulator());
  }

  private BooleanStatsAccumulator(final BooleanHistogramAccumulator histogramAccumulator) {
    this.histogramAccumulator = histogramAccumulator;
  }

  public void add(final Boolean value) {
    this.histogramAccumulator.add(value);
  }

  public static BooleanStatsAccumulator fromSnapshot(final Stats.BooleanStats stats) {
    final var histogramAccumulator = BooleanHistogramAccumulator.fromSnapshot(stats.getHistogram());
    return new BooleanStatsAccumulator(histogramAccumulator);
  }

  @Override
  public Stats.BooleanStats snapshot() {
    final var histogram = histogramAccumulator.snapshot();
    final var count =
        histogram
            .getBucketsList()
            .stream()
            .mapToLong(Histogram.BooleanHistogram.Bucket::getCount)
            .sum();
    return Stats.BooleanStats.newBuilder().setHistogram(histogram).setCount(count).build();
  }
}
