package com.hilohq.hilo.parsers.logparse.log;

import com.hilohq.hilo.parsers.field.FieldKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TypeInfoStats<K extends FieldKey, T> {
  private final Map<K, TypeInfoFieldStats<T>> typeInfoFieldStats;

  public TypeInfoStats() {
    this.typeInfoFieldStats = new HashMap<>();
  }

  public Set<Map.Entry<K, TypeInfoFieldStats<T>>> fieldStats() {
    return typeInfoFieldStats.entrySet();
  }

  public void observe(final K key, final T type) {
    if (!typeInfoFieldStats.containsKey(key)) {
      typeInfoFieldStats.put(key, new TypeInfoFieldStats<>());
    }

    typeInfoFieldStats.get(key).observe(type);
  }
}
