package com.hilohq.hilo.features.stats.numbers;

import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass.NumericStats.PercentileHistogram;

public final class DifferentialPercentileHistogram {

  private final NumericPercentileHistogram histogram;
  private Double previous;

  public DifferentialPercentileHistogram() {
    this(new NumericPercentileHistogram());
  }

  private DifferentialPercentileHistogram(final NumericPercentileHistogram histogram) {
    this.histogram = histogram;
    this.previous = null;
  }

  public void observe(final Double value) {
    if (previous == null) {
      previous = value;
      return;
    }

    final var diff = value - previous;
    histogram.observe(diff);
  }

  public void merge(final DifferentialPercentileHistogram other) {
    histogram.merge(other.histogram);
    previous = null;
  }

  public PercentileHistogram snapshot() {
    return histogram.snapshot();
  }

  public static DifferentialPercentileHistogram fromSnapshot(final PercentileHistogram snapshot) {
    return new DifferentialPercentileHistogram(NumericPercentileHistogram.fromSnapshot(snapshot));
  }
}
