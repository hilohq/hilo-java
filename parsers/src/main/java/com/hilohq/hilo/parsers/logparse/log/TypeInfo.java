package com.hilohq.hilo.parsers.logparse.log;

import com.hilohq.hilo.parsers.field.FieldKey;
import java.util.Map;

public final class TypeInfo<K extends FieldKey, T> {
  private final Map<K, T> typeInfo;

  public TypeInfo(final Map<K, T> typeInfo) {
    this.typeInfo = typeInfo;
  }

  public int size() {
    return typeInfo.size();
  }

  public T get(final K key) {
    return typeInfo.get(key);
  }

  public boolean containsKey(final K key) {
    return typeInfo.containsKey(key);
  }

  public Map<K, T> typeInfo() {
    return typeInfo;
  }
}
