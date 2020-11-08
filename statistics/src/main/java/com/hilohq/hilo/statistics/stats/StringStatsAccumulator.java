package com.hilohq.hilo.statistics.stats;

import com.hilohq.hilo.protobuf.stats.Histogram;
import com.hilohq.hilo.protobuf.stats.Stats;

public final class StringStatsAccumulator implements StatsAccumulator<String, Stats.StringStats> {

  private final StringHistogramAccumulator histogramAccumulator;

  public StringStatsAccumulator() {
    this(new StringHistogramAccumulator());
  }

  private StringStatsAccumulator(final StringHistogramAccumulator histogramAccumulator) {
    this.histogramAccumulator = histogramAccumulator;
  }

  public void add(final String value) {
    histogramAccumulator.add(value);
  }

  public static StringStatsAccumulator fromSnapshot(final Stats.StringStats snapshot) {
    return new StringStatsAccumulator(
        StringHistogramAccumulator.fromSnapshot(snapshot.getHistogram()));
  }

  @Override
  public Stats.StringStats snapshot() {
    final var histogram = histogramAccumulator.snapshot();
    final var count =
        histogram
            .getBucketsList()
            .stream()
            .mapToLong(Histogram.StringHistogram.Bucket::getCount)
            .sum();

    return Stats.StringStats.newBuilder().setCount(count).setHistogram(histogram).build();
  }
}
