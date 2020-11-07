package com.hilohq.hilo.statistics.stats;

public interface StatsAccumulator<T> {
    void add(T value);
}
