package com.hilohq.hilo.features.stats.strings;

import com.hilohq.hilo.features.stats.feature.FeatureStats;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;
import java.nio.charset.StandardCharsets;

public final class BytesStats implements FeatureStats {

  private final StringStats stats;

  public BytesStats() {
    this.stats = new StringStats();
  }

  @Override
  public void observe(Value value) {
    stats.observe(new String(value.bytesValue(), StandardCharsets.UTF_8));
  }

  @Override
  public FeatureOuterClass.FeatureStats snapshot() {
    return stats.snapshot();
  }
}
