package com.hilohq.hilo.parsers.logparse.log;

import com.hilohq.hilo.parsers.field.FieldsMap;

public interface LogParser {
  FieldsMap parse(String log);
}
