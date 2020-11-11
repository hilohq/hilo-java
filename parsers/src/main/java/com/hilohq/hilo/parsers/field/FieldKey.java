package com.hilohq.hilo.parsers.field;

public interface FieldKey {
  String key();
  boolean equals(Object o);
  int hashCode();
}
