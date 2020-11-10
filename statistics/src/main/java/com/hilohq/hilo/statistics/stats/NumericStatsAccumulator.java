package com.hilohq.hilo.statistics.stats;

import com.hilohq.hilo.protobuf.stats.Stats;
import java.util.Base64;

public final class NumericStatsAccumulator implements TypeStatsAccumulator<Double> {
  private final com.google.common.math.StatsAccumulator statsAccumulator;
  private final NumericPercentileHistogramAccumulator valueHistogramAccumulator;
  private final DifferentialPercentileHistogramAccumulator differentialHistogramAccumulator;

  private NumericStatsAccumulator(
      final com.google.common.math.StatsAccumulator statsAccumulator,
      final NumericPercentileHistogramAccumulator valueHistogramAccumulator,
      final DifferentialPercentileHistogramAccumulator differentialHistogramAccumulator) {
    this.statsAccumulator = statsAccumulator;
    this.valueHistogramAccumulator = valueHistogramAccumulator;
    this.differentialHistogramAccumulator = differentialHistogramAccumulator;
  }

  public NumericStatsAccumulator() {
    this(
        new com.google.common.math.StatsAccumulator(),
        new NumericPercentileHistogramAccumulator(),
        new DifferentialPercentileHistogramAccumulator());
  }

  @Override
  public void add(final Double value) {
    statsAccumulator.add(value);
    valueHistogramAccumulator.add(value);
  }

  public static NumericStatsAccumulator fromSnapshot(final Stats.TypeStats stats) {
    if (!stats.hasNumericStats()) {
      throw new IllegalArgumentException(
          "NumericStatsAccumulator expects an instance of TypeStats " + "with NumericStats set");
    }

    return fromSnapshot(stats.getNumericStats());
  }

  public static NumericStatsAccumulator fromSnapshot(final Stats.NumericStats stats) {
    final var valueHistogramAccumulator =
        NumericPercentileHistogramAccumulator.fromSnapshot(stats.getValueHistogram());
    final var differentialHistogramAccumulator =
        DifferentialPercentileHistogramAccumulator.fromSnapshot(stats.getDifferentialHistogram());
    final var internalStatsAccumulator = new com.google.common.math.StatsAccumulator();
    final var internalStats =
        com.google.common.math.Stats.fromByteArray(
            Base64.getDecoder().decode(stats.getBase64StatsRepr()));
    internalStatsAccumulator.addAll(internalStats);
    return new NumericStatsAccumulator(
        internalStatsAccumulator, valueHistogramAccumulator, differentialHistogramAccumulator);
  }

  @Override
  public Stats.TypeStats snapshot() {
    return Stats.TypeStats.newBuilder()
        .setNumericStats(
            Stats.NumericStats.newBuilder()
                .setCount(statsAccumulator.count())
                .setMax(statsAccumulator.max())
                .setMin(statsAccumulator.min())
                .setMean(statsAccumulator.mean())
                .setStandardDeviation(statsAccumulator.sampleStandardDeviation())
                .setValueHistogram(valueHistogramAccumulator.snapshot())
                .setDifferentialHistogram(differentialHistogramAccumulator.snapshot())
                .setBase64StatsRepr(
                    Base64.getEncoder().encodeToString(statsAccumulator.snapshot().toByteArray()))
                .build())
        .build();
  }
}
