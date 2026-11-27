package com.hilohq.hilo.features.stats.numbers;

import com.google.common.math.StatsAccumulator;
import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.Base64;

public final class DoubleStats implements StatsCollector, Snapshot<FeatureOuterClass.NumericStats> {
  private static final int DEFAULT_WINDOW_SIZE = 16;

  private final StatsAccumulator valueStatsAccumulator;
  private final ContinuityStats continuityStats;
  private final NumericPercentileHistogram valueHistogram;
  private final NumericPercentileHistogram differentialHistogram;
  private final Window valueWindow;

  private DoubleStats(
      final StatsAccumulator valueStatsAccumulator,
      final ContinuityStats continuityStats,
      final NumericPercentileHistogram valueHistogram,
      final NumericPercentileHistogram differentialHistogram) {
    this.valueStatsAccumulator = valueStatsAccumulator;
    this.continuityStats = continuityStats;
    this.valueHistogram = valueHistogram;
    this.differentialHistogram = differentialHistogram;
    this.valueWindow = new Window(DEFAULT_WINDOW_SIZE);
  }

  public DoubleStats() {
    this(
        new StatsAccumulator(),
        new ContinuityStats(),
        new NumericPercentileHistogram(),
        new NumericPercentileHistogram());
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
    valueStatsAccumulator.addAll(other.valueStatsAccumulator);
    continuityStats.merge(other.continuityStats);

    valueHistogram.merge(other.valueHistogram);
    differentialHistogram.merge(other.differentialHistogram);
    valueWindow.clear();
  }

  public void observe(final Double value) {
    final var previousOptional = valueWindow.peek();
    valueStatsAccumulator.add(value);
    valueHistogram.observe(value);
    valueWindow.observe(value);
    previousOptional.ifPresent(previous -> differentialHistogram.observe(value - previous));
    continuityStats.observe(value);
  }

  public static DoubleStats fromSnapshot(final FeatureOuterClass.NumericStats stats) {
    final var valueHistogram = NumericPercentileHistogram.fromSnapshot(stats.getValueHistogram());
    final var differentialHistogram =
        NumericPercentileHistogram.fromSnapshot(stats.getDifferentialHistogram());
    final var valueStatsAccumulator = new com.google.common.math.StatsAccumulator();
    final var valueStats =
        com.google.common.math.Stats.fromByteArray(
            Base64.getDecoder().decode(stats.getValueStats().getBase64StatsRepr()));
    valueStatsAccumulator.addAll(valueStats);
    final var continuityStats = ContinuityStats.fromSnapshot(stats.getContinuityStats());
    return new DoubleStats(
        valueStatsAccumulator, continuityStats, valueHistogram, differentialHistogram);
  }

  public FeatureOuterClass.NumericStats snapshot() {
    final var valueStats =
        FeatureOuterClass.NumericStats.ValueStats.newBuilder()
            .setCount(valueStatsAccumulator.count())
            .setBase64StatsRepr(
                Base64.getEncoder().encodeToString(valueStatsAccumulator.snapshot().toByteArray()));
    // Guava throws for min/max/mean of an empty sample and for the sample deviation of one value.
    if (valueStatsAccumulator.count() > 0) {
      valueStats
          .setMax(valueStatsAccumulator.max())
          .setMin(valueStatsAccumulator.min())
          .setMean(valueStatsAccumulator.mean());
    }
    if (valueStatsAccumulator.count() > 1) {
      valueStats.setStandardDeviation(valueStatsAccumulator.sampleStandardDeviation());
    }

    return FeatureOuterClass.NumericStats.newBuilder()
        .setValueStats(valueStats)
        .setContinuityStats(continuityStats.snapshot())
        .setValueHistogram(valueHistogram.snapshot())
        .setDifferentialHistogram(differentialHistogram.snapshot())
        .build();
  }
}
