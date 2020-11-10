package com.hilohq.hilo.parsers.field;

import java.util.Map;

public final class FieldsStats {
    public final Map<FieldKey, TypeStats> stats;

    public FieldsStats(final Map<FieldKey, TypeStats> stats) {
        this.stats = stats;
    }

    public TypeStats value(final FieldKey key) {
        return stats.get(key);
    }
}
