package com.hilohq.hilo.features.stats.numbers;

import com.hilohq.hilo.features.stats.feature.FeatureStats;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;
import java.util.Base64;

public final class DoubleStats implements FeatureStats {
  private final com.google.common.math.StatsAccumulator statsAccumulator;
  private final NumericPercentileHistogram valueHistogramAccumulator;
  private final DifferentialPercentileHistogram differentialHistogramAccumulator;

  private DoubleStats(
      final com.google.common.math.StatsAccumulator statsAccumulator,
      final NumericPercentileHistogram valueHistogramAccumulator,
      final DifferentialPercentileHistogram differentialHistogramAccumulator) {
    this.statsAccumulator = statsAccumulator;
    this.valueHistogramAccumulator = valueHistogramAccumulator;
    this.differentialHistogramAccumulator = differentialHistogramAccumulator;
  }

  public DoubleStats() {
    this(
        new com.google.common.math.StatsAccumulator(),
        new NumericPercentileHistogram(),
        new DifferentialPercentileHistogram());
  }

  @Override
  public void observe(final Value value) {
    observe(value.doubleValue());
  }

  public void observe(final Double value) {
    statsAccumulator.add(value);
    valueHistogramAccumulator.observe(value);
  }

  public static DoubleStats fromSnapshot(final FeatureOuterClass.FeatureStats stats) {
    if (!stats.hasNumericStats()) {
      throw new IllegalArgumentException(
          "NumericStatsAccumulator expects an instance of FeatureStats " + "with NumericStats set");
    }

    return fromSnapshot(stats.getNumericStats());
  }

  public static DoubleStats fromSnapshot(final FeatureOuterClass.NumericStats stats) {
    final var valueHistogramAccumulator =
        NumericPercentileHistogram.fromSnapshot(stats.getValueHistogram());
    final var differentialHistogramAccumulator =
        DifferentialPercentileHistogram.fromSnapshot(stats.getDifferentialHistogram());
    final var internalStatsAccumulator = new com.google.common.math.StatsAccumulator();
    final var internalStats =
        com.google.common.math.Stats.fromByteArray(
            Base64.getDecoder().decode(stats.getBase64StatsRepr()));
    internalStatsAccumulator.addAll(internalStats);
    return new DoubleStats(
        internalStatsAccumulator, valueHistogramAccumulator, differentialHistogramAccumulator);
  }

  public FeatureOuterClass.FeatureStats snapshot() {
    return FeatureOuterClass.FeatureStats.newBuilder()
        .setNumericStats(
            FeatureOuterClass.NumericStats.newBuilder()
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
