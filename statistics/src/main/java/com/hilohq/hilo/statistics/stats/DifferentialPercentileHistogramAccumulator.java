package com.hilohq.hilo.statistics.stats;

import com.hilohq.hilo.protobuf.stats.Histogram;

public final class DifferentialPercentileHistogramAccumulator
    implements StatsAccumulator<Double, Histogram.NumericPercentileHistogram> {

  private final NumericPercentileHistogramAccumulator histogramAccumulator;
  private Double previous;

  public DifferentialPercentileHistogramAccumulator() {
    this(new NumericPercentileHistogramAccumulator());
  }

  private DifferentialPercentileHistogramAccumulator(
      final NumericPercentileHistogramAccumulator histogramAccumulator) {
    this.histogramAccumulator = histogramAccumulator;
    this.previous = null;
  }

  @Override
  public void add(final Double value) {
    if (previous == null) {
      previous = value;
      return;
    }

    final var diff = value - previous;
    histogramAccumulator.add(diff);
  }

  @Override
  public Histogram.NumericPercentileHistogram snapshot() {
    return histogramAccumulator.snapshot();
  }

  public static DifferentialPercentileHistogramAccumulator fromSnapshot(
      final Histogram.NumericPercentileHistogram histogram) {
    final var percentileAccumulator = NumericPercentileHistogramAccumulator.fromSnapshot(histogram);
    return new DifferentialPercentileHistogramAccumulator(percentileAccumulator);
  }
}
