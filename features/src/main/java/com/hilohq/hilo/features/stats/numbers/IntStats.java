package com.hilohq.hilo.features.stats.numbers;

import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;

public final class IntStats implements StatsCollector, Snapshot<FeatureOuterClass.NumericStats> {

  private final DoubleStats stats;

  private IntStats(final DoubleStats stats) {
    this.stats = stats;
  }

  public IntStats() {
    this(new DoubleStats());
  }

  @Override
  public void observe(final Value value) {
    stats.observe((double) value.intValue());
  }

  @Override
  public void merge(final StatsCollector collector) {
    if (!collector.getClass().equals(IntStats.class)) {
      throw new IllegalArgumentException(
          "IntStats expects another " + "instance of IntStats for a merge");
    }

    final var other = (IntStats) collector;
    stats.merge(other.stats);
  }

  public static IntStats fromSnapshot(final FeatureOuterClass.NumericStats snapshot) {
    final var stats = DoubleStats.fromSnapshot(snapshot);
    return new IntStats(stats);
  }

  @Override
  public FeatureOuterClass.NumericStats snapshot() {
    return stats.snapshot();
  }
}
