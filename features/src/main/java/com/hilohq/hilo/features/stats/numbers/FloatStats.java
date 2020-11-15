package com.hilohq.hilo.features.stats.numbers;

import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;

public class FloatStats implements StatsCollector, Snapshot<FeatureOuterClass.NumericStats> {

  private final DoubleStats stats;

  public FloatStats(final DoubleStats stats) {
    this.stats = stats;
  }

  public FloatStats() {
    this(new DoubleStats());
  }

  @Override
  public void observe(final Value value) {
    stats.observe((double) value.floatValue());
  }

  @Override
  public void merge(final StatsCollector collector) {
    if (!collector.getClass().equals(FloatStats.class)) {
      throw new IllegalArgumentException(
          "FloatStats expects another " + "instance of FloatStats for a merge");
    }

    final var other = (FloatStats) collector;
    stats.merge(other.stats);
  }

  public static FloatStats fromSnapshot(final FeatureOuterClass.NumericStats snapshot) {
    final var stats = DoubleStats.fromSnapshot(snapshot);
    return new FloatStats(stats);
  }

  @Override
  public FeatureOuterClass.NumericStats snapshot() {
    return stats.snapshot();
  }
}
