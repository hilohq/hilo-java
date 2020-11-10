package com.hilohq.hilo.statistics.stats;

import com.hilohq.hilo.protobuf.stats.Histogram;
import com.hilohq.hilo.protobuf.stats.Stats;

public final class StringStatsAccumulator implements TypeStatsAccumulator<String> {

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

  public static StringStatsAccumulator fromSnapshot(final Stats.TypeStats stats) {
    if (!stats.hasStringStats()) {
      throw new IllegalArgumentException(
          "StringStatsAccumulator expects an instance of TypeStats " + "with StringStats set");
    }

    return fromSnapshot(stats.getStringStats());
  }

  @Override
  public Stats.TypeStats snapshot() {
    final var histogram = histogramAccumulator.snapshot();
    final var count =
        histogram
            .getBucketsList()
            .stream()
            .mapToLong(Histogram.StringHistogram.Bucket::getCount)
            .sum();

    return Stats.TypeStats.newBuilder()
        .setStringStats(
            Stats.StringStats.newBuilder().setCount(count).setHistogram(histogram).build())
        .build();
  }
}
