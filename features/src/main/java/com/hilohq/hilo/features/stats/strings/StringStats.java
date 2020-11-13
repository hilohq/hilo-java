package com.hilohq.hilo.features.stats.strings;

import com.hilohq.hilo.features.stats.feature.FeatureStats;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;

public final class StringStats implements FeatureStats {

  private final StringHistogram histogram;

  public StringStats() {
    this(new StringHistogram());
  }

  private StringStats(final StringHistogram histogram) {
    this.histogram = histogram;
  }

  @Override
  public void observe(final Value value) {
    observe(value.stringValue());
  }

  public void observe(final String value) {
    histogram.add(value);
  }

  public static StringStats fromSnapshot(final FeatureOuterClass.StringStats snapshot) {
    return new StringStats(StringHistogram.fromSnapshot(snapshot.getHistogram()));
  }

  @Override
  public FeatureOuterClass.FeatureStats snapshot() {
    final var histogramSnapshot = histogram.snapshot();
    final var count =
        histogramSnapshot
            .getBucketsList()
            .stream()
            .mapToLong(FeatureOuterClass.StringStats.Histogram.Bucket::getCount)
            .sum();

    return FeatureOuterClass.FeatureStats.newBuilder()
        .setStringStats(
            FeatureOuterClass.StringStats.newBuilder()
                .setCount(count)
                .setHistogram(histogramSnapshot))
        .build();
  }
}
