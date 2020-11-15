package com.hilohq.hilo.features.stats.strings;

import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;

public final class StringStats implements StatsCollector, Snapshot<FeatureOuterClass.StringStats> {

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

  @Override
  public void merge(final StatsCollector collector) {
    if (!collector.getClass().equals(StringStats.class)) {
      throw new IllegalArgumentException(
          "StringStats expects another " + "instance of StringStats for a merge");
    }

    final var other = (StringStats) collector;
    for (final var entry : other.histogram.entrySet()) {
      histogram.observe(entry.getKey(), entry.getValue());
    }
  }

  public void observe(final String value) {
    histogram.observe(value);
  }

  public static StringStats fromSnapshot(final FeatureOuterClass.FeatureStats snapshot) {
    if (!snapshot.hasStringStats()) {
      throw new IllegalArgumentException("snapshot of StringStats expected");
    }

    return fromSnapshot(snapshot.getStringStats());
  }

  public static StringStats fromSnapshot(final FeatureOuterClass.StringStats snapshot) {
    return new StringStats(StringHistogram.fromSnapshot(snapshot.getHistogram()));
  }

  @Override
  public FeatureOuterClass.StringStats snapshot() {
    final var histogramSnapshot = histogram.snapshot();
    final var count =
        histogramSnapshot
            .getBucketsList()
            .stream()
            .mapToLong(FeatureOuterClass.StringStats.Histogram.Bucket::getCount)
            .sum();

    return FeatureOuterClass.StringStats.newBuilder()
        .setCount(count)
        .setHistogram(histogramSnapshot)
        .build();
  }
}
