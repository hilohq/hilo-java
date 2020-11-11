package com.hilohq.hilo.parsers.logparse.log;

import com.hilohq.hilo.parsers.field.FieldKey;

@FunctionalInterface
public interface Infer<K extends FieldKey, T> {
  TypeInfo<K, T> infer(String log);
}
