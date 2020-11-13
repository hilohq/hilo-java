package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;

public final class EmptyStats implements FeatureStats {
  public EmptyStats() {}

  @Override
  public void observe(final Value value) {}

  @Override
  public FeatureOuterClass.FeatureStats snapshot() {
    return FeatureOuterClass.FeatureStats.newBuilder().build();
  }
}
