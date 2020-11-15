package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.parsers.value.Value;

public final class EmptyStats implements StatsCollector {
  public EmptyStats() {}

  @Override
  public void observe(final Value value) {}

  @Override
  public void merge(final StatsCollector collector) {}
}
