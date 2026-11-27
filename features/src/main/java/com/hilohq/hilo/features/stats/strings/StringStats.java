package com.hilohq.hilo.features.stats.strings;

import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;

public final class StringStats implements StatsCollector, Snapshot<FeatureOuterClass.StringStats> {

  private final StringHistogram histogram;
  // Kept separately from the histogram, which stops tracking (and reports no buckets) once it
  // sees too many distinct values.
  private long count;

  public StringStats() {
    this(new StringHistogram(), 0);
  }

  private StringStats(final StringHistogram histogram, final long count) {
    this.histogram = histogram;
    this.count = count;
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
    histogram.merge(other.histogram);
    count += other.count;
  }

  public void observe(final String value) {
    histogram.observe(value);
    count++;
  }

  public static StringStats fromSnapshot(final FeatureOuterClass.FeatureStats snapshot) {
    if (!snapshot.hasStringStats()) {
      throw new IllegalArgumentException("snapshot of StringStats expected");
    }

    return fromSnapshot(snapshot.getStringStats());
  }

  public static StringStats fromSnapshot(final FeatureOuterClass.StringStats snapshot) {
    return new StringStats(
        StringHistogram.fromSnapshot(snapshot.getHistogram()), snapshot.getCount());
  }

  @Override
  public FeatureOuterClass.StringStats snapshot() {
    return FeatureOuterClass.StringStats.newBuilder()
        .setCount(count)
        .setHistogram(histogram.snapshot())
        .build();
  }
}
