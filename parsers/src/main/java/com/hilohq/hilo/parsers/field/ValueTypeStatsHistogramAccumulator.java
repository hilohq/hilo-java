package com.hilohq.hilo.parsers.field;

import com.hilohq.hilo.parsers.value.ValueType;

import java.util.HashMap;
import java.util.Map;

public final class ValueTypeStatsHistogramAccumulator {

    private final Map<ValueType, Long> typeCount;

    public ValueTypeStatsHistogramAccumulator() {
        typeCount = new HashMap<>();
    }

    public void add(final ValueType type) {
        add(type, 1);
    }

    public void add(final ValueType type, final long count) {
        if (!typeCount.containsKey(type)) {
            typeCount.put(type, 0L);
        }

        typeCount.put(type, typeCount.get(type) + count);
    }

    public ValueTypeStatsHistogram snapshot() {
        final var snapshot = new HashMap<ValueType, Long>();
        for (final var entry : typeCount.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue());
        }
        return new ValueTypeStatsHistogram(snapshot);
    }
}
