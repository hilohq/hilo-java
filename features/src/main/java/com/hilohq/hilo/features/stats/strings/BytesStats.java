package com.hilohq.hilo.features.stats.strings;

import com.hilohq.hilo.features.stats.feature.Snapshot;
import com.hilohq.hilo.features.stats.feature.StatsCollector;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.nio.charset.StandardCharsets;

public final class BytesStats implements StatsCollector, Snapshot<FeatureOuterClass.StringStats> {

  private final StringStats stats;

  public BytesStats(final StringStats stats) {
    this.stats = stats;
  }

  public BytesStats() {
    this(new StringStats());
  }

  @Override
  public void observe(Value value) {
    stats.observe(new String(value.bytesValue(), StandardCharsets.UTF_8));
  }

  @Override
  public void merge(final StatsCollector collector) {
    if (!collector.getClass().equals(BytesStats.class)) {
      throw new IllegalArgumentException(
          "BytesStats expects another " + "instance of BytesStats for a merge");
    }

    final var other = (BytesStats) collector;
    stats.merge(other.stats);
  }

  public static BytesStats fromSnapshot(final FeatureOuterClass.StringStats snapshot) {
    final var stats = StringStats.fromSnapshot(snapshot);
    return new BytesStats(stats);
  }

  @Override
  public FeatureOuterClass.StringStats snapshot() {
    return stats.snapshot();
  }
}
