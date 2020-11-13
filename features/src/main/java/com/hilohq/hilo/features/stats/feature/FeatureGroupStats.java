package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.features.stats.booleans.BooleanStats;
import com.hilohq.hilo.features.stats.numbers.DoubleStats;
import com.hilohq.hilo.features.stats.numbers.FloatStats;
import com.hilohq.hilo.features.stats.numbers.IntStats;
import com.hilohq.hilo.features.stats.numbers.LongStats;
import com.hilohq.hilo.features.stats.strings.BytesStats;
import com.hilohq.hilo.features.stats.strings.StringStats;
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

  private void addNewFeatureStats(final String key, final Value value) {
    switch (value.type()) {
      case Int:
        featureStats.put(key, new IntStats());
        break;
      case Long:
        featureStats.put(key, new LongStats());
        break;
      case Float:
        featureStats.put(key, new FloatStats());
        break;
      case Double:
        featureStats.put(key, new DoubleStats());
        break;
      case String:
        featureStats.put(key, new StringStats());
        break;
      case Bytes:
        featureStats.put(key, new BytesStats());
        break;
      case Boolean:
        featureStats.put(key, new BooleanStats());
        break;
      case Null:
        featureStats.put(key, new EmptyStats());
        break;
      default:
        throw new IllegalArgumentException("Unexpected value type " + value.type());
    }
  }

  public void observe(final String key, final Value value) {
    if (!featureStats.containsKey(key)) {
      addNewFeatureStats(key, value);
    }

    featureStats.get(key).observe(value);
  }
}
