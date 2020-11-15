package com.hilohq.hilo.features.stats.numbers;

import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.Base64;

public final class DoubleStats implements StatsCollector, Snapshot<FeatureOuterClass.NumericStats> {
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

  @Override
  public void merge(final StatsCollector collector) {
    if (!collector.getClass().equals(DoubleStats.class)) {
      throw new IllegalArgumentException(
          "DoubleStats expects another " + "instance of DoubleStats for a merge");
    }

    final var other = (DoubleStats) collector;
    statsAccumulator.addAll(other.statsAccumulator);

    valueHistogramAccumulator.merge(other.valueHistogramAccumulator);
    differentialHistogramAccumulator.merge(other.differentialHistogramAccumulator);
  }

  public void observe(final Double value) {
    statsAccumulator.add(value);
    valueHistogramAccumulator.observe(value);
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

  public FeatureOuterClass.NumericStats snapshot() {
    return FeatureOuterClass.NumericStats.newBuilder()
        .setCount(statsAccumulator.count())
        .setMax(statsAccumulator.max())
        .setMin(statsAccumulator.min())
        .setMean(statsAccumulator.mean())
        .setStandardDeviation(statsAccumulator.sampleStandardDeviation())
        .setValueHistogram(valueHistogramAccumulator.snapshot())
        .setDifferentialHistogram(differentialHistogramAccumulator.snapshot())
        .setBase64StatsRepr(
            Base64.getEncoder().encodeToString(statsAccumulator.snapshot().toByteArray()))
        .build();
  }
}
