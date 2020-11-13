package com.hilohq.hilo.parsers.logparse.log;

import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.FieldsMap;

public interface LogParser<K extends FieldKey> {
  FieldsMap<K> parse(String log);
}
