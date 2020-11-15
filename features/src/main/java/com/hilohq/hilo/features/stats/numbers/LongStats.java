package com.hilohq.hilo.features.stats.numbers;

import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;

public class LongStats implements StatsCollector, Snapshot<FeatureOuterClass.NumericStats> {

  private final DoubleStats stats;

  private LongStats(final DoubleStats stats) {
    this.stats = stats;
  }

  public LongStats() {
    this(new DoubleStats());
  }

  @Override
  public void observe(final Value value) {
    stats.observe((double) value.longValue());
  }

  @Override
  public void merge(final StatsCollector collector) {
    if (!collector.getClass().equals(LongStats.class)) {
      throw new IllegalArgumentException(
          "LongStats expects another " + "instance of LongStats for a merge");
    }

    final var other = (LongStats) collector;
    stats.merge(other.stats);
  }

  public static LongStats fromSnapshot(final FeatureOuterClass.NumericStats snapshot) {
    final var stats = DoubleStats.fromSnapshot(snapshot);
    return new LongStats(stats);
  }

  @Override
  public FeatureOuterClass.NumericStats snapshot() {
    return stats.snapshot();
  }
}
