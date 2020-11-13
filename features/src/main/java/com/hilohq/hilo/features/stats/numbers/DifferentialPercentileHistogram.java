package com.hilohq.hilo.features.stats.numbers;

import static com.hilohq.hilo.protobufs.structure.FeatureOuterClass.*;
import static com.hilohq.hilo.protobufs.structure.FeatureOuterClass.NumericStats.*;

public final class DifferentialPercentileHistogram {

  private final NumericPercentileHistogram histogramAccumulator;
  private Double previous;

  public DifferentialPercentileHistogram() {
    this(new NumericPercentileHistogram());
  }

  private DifferentialPercentileHistogram(final NumericPercentileHistogram histogramAccumulator) {
    this.histogramAccumulator = histogramAccumulator;
    this.previous = null;
  }

  public void observe(final Double value) {
    if (previous == null) {
      previous = value;
      return;
    }

    final var diff = value - previous;
    histogramAccumulator.observe(diff);
  }

  public PercentileHistogram snapshot() {
    return histogramAccumulator.snapshot();
  }

  public static DifferentialPercentileHistogram fromSnapshot(final PercentileHistogram snapshot) {
    return new DifferentialPercentileHistogram(NumericPercentileHistogram.fromSnapshot(snapshot));
  }
}
