package com.hilohq.hilo.statistics.stats;

public final class IntStatsAccumulator implements StatsAccumulator<Integer> {
    private final com.google.common.math.StatsAccumulator statsAccumulator;

    public IntStatsAccumulator() {
        this.statsAccumulator = new com.google.common.math.StatsAccumulator();
    }

    @Override
    public void add(final Integer value) {
        statsAccumulator.add(value);
    }


}
