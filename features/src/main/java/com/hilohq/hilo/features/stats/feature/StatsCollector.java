package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.parsers.value.Value;

public interface StatsCollector {
  void observe(Value value);

  void merge(StatsCollector collector);
}
