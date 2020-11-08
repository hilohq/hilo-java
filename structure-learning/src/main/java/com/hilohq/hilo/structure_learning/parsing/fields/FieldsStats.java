package com.hilohq.hilo.structure_learning.parsing.fields;

import java.util.HashMap;
import java.util.Map;

public final class FieldsStats {
  private final Map<String, FieldStats> stats;

  public FieldsStats() {
    this.stats = new HashMap<>();
  }

  public Map<String, FieldStats> stats() {
    return stats;
  }
}
