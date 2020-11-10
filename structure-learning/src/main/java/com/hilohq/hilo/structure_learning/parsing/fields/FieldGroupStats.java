package com.hilohq.hilo.structure_learning.parsing.fields;

import com.hilohq.hilo.statistics.stats.TypeStatsAccumulator;
import java.util.HashMap;
import java.util.Map;

public final class FieldGroupStats {
  private final Map<String, TypeStatsAccumulator<?>> stats;

  public FieldGroupStats() {
    this.stats = new HashMap<>();
  }

  public Map<String, TypeStatsAccumulator<?>> stats() {
    return stats;
  }
}
