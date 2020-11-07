package com.hilohq.hilo.statistics.stats;

import com.google.protobuf.Message;

public interface StatsAccumulator<T, V extends Message> extends Snapshot<V> {
  void add(T value);
}
