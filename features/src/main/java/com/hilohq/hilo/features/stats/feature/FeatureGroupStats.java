package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.parsers.value.Value;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class FeatureGroupStats {

  private final Map<String, FeatureStats> featureStats;

  public FeatureGroupStats() {
    this.featureStats = new HashMap<>();
  }

  public Set<Map.Entry<String, FeatureStats>> featureStats() {
    return featureStats.entrySet();
  }

  public void observe(final String key, final Value value) {
    if (!featureStats.containsKey(key)) {
      featureStats.put(key, new FeatureStats());
    }

    featureStats.get(key).observe(value);
  }
}
