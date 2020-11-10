package com.hilohq.hilo.parsers.field;

import com.hilohq.hilo.parsers.value.ValueType;

import java.util.Map;
import java.util.Set;

public final class ValueTypeStatsHistogram {
    private final Map<ValueType, Long> typeCount;

    ValueTypeStatsHistogram(final Map<ValueType, Long> typeCount) {
        this.typeCount = typeCount;
    }

    public Set<Map.Entry<ValueType, Long>> entrySet() {
        return typeCount.entrySet();
    }
}
