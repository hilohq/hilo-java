package com.hilohq.hilo.parsers.field;

import com.hilohq.hilo.parsers.value.Value;
import java.util.HashMap;
import java.util.Map;

public final class FieldsMap<K extends FieldKey> {
  public final Map<K, Value> values;

  public FieldsMap(final Map<K, Value> values) {
    this.values = values;
  }

  public FieldsMap() {
    this(new HashMap<>());
  }

  public Value put(final K key, final Value value) {
    return values.put(key, value);
  }

  public int size() {
    return values.size();
  }

  public Value get(final K key) {
    return values.get(key);
  }

  public Iterable<K> keys() {
    return values.keySet();
  }

  public Iterable<Value> values() {
    return values.values();
  }

  public Iterable<Map.Entry<K, Value>> entrySet() {
    return values.entrySet();
  }

  public Value value(final K key) {
    return values.get(key);
  }
}
