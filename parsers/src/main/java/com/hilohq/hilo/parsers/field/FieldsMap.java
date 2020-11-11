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

  public Value put(final FieldKey key, final Value value) {
    return values.put(key, value);
  }

  public int size() {
    return values.size();
  }

  public Value get(final FieldKey key) {
    return values.get(key);
  }

  public Iterable<FieldKey> keys() {
    return values.keySet();
  }

  public Iterable<Value> values() {
    return values.values();
  }

  public Iterable<Map.Entry<FieldKey, Value>> entrySet() {
    return values.entrySet();
  }

  public Value value(final FieldKey key) {
    return values.get(key);
  }
}
