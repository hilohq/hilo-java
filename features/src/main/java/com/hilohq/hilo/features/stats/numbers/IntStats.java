package com.hilohq.hilo.features.stats.numbers;

import com.hilohq.hilo.features.stats.feature.FeatureStats;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;

public final class IntStats implements FeatureStats {

  private final DoubleStats stats;

  public IntStats() {
    this.stats = new DoubleStats();
  }

  @Override
  public void observe(final Value value) {
    stats.observe((double) value.intValue());
  }

  @Override
  public FeatureOuterClass.FeatureStats snapshot() {
    return stats.snapshot();
  }
}
