package com.hilohq.hilo.parsers.field;

import com.hilohq.hilo.parsers.value.ValueType;

public final class ValueTypeStatsAccumulator {
    private final ValueTypeStatsHistogramAccumulator histogramAccumulator;

    public ValueTypeStatsAccumulator() {
        this.histogramAccumulator = new ValueTypeStatsHistogramAccumulator();
    }

    public void add(final ValueType type) {
        histogramAccumulator.add(type);
    }

    public void addAll(final TypeStats stats) {
        for (final var entry : stats.histogram().entrySet()) {
            histogramAccumulator.add(entry.getKey(), entry.getValue());
        }
    }

    public TypeStats snapshot() {
        return new TypeStats(histogramAccumulator.snapshot());
    }
}
