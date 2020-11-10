package com.hilohq.hilo.parsers.field;

import com.hilohq.hilo.parsers.value.Value;

import java.util.HashMap;
import java.util.Map;

public final class FieldsMap {
    public final Map<FieldKey, Value> values;

    public FieldsMap(final Map<FieldKey, Value> values) {
        this.values = values;
    }

    public FieldsMap() {
        this(new HashMap<>());
    }

    public Iterable<FieldKey> keys() {
        return values.keySet();
    }

    public Iterable<Value> values() {
        return values.values();
    }

    public Value value(final FieldKey key) {
        return values.get(key);
    }
}
