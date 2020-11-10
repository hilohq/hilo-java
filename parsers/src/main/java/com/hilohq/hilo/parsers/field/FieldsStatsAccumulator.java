package com.hilohq.hilo.parsers.field;

import com.hilohq.hilo.parsers.value.Value;

import java.util.HashMap;
import java.util.Map;

public final class FieldsStatsAccumulator {
    public final Map<FieldKey, ValueTypeStatsAccumulator> values;

    public FieldsStatsAccumulator() {
        values = new HashMap<>();
    }

    public void add(final FieldKey key, final Value value) {
        if (!values.containsKey(key)) {
            values.put(key, new ValueTypeStatsAccumulator());
        }
        values.get(key).add(value);
    }

    public void addAll(final FieldsMap fields) {
        for (final var key : fields.keys()) {
            add(key, fields.value(key));
        }
    }

    public FieldsStats snapshot() {
        final var snapshot = new HashMap<FieldKey, TypeStats>();
        for (final var entry : values.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().snapshot());
        }
        return new FieldsStats(snapshot);
    }
}
