package com.hilohq.hilo.parsers.field;

public final class TypeStats {
    private final ValueTypeStatsHistogram histogram;

    TypeStats(final ValueTypeStatsHistogram histogram) {
        this.histogram = histogram;
    }

    public ValueTypeStatsHistogram histogram() {
        return histogram;
    }
}
