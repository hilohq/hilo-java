package com.hilohq.hilo.parsers.logparse.log;

import com.hilohq.hilo.parsers.field.FieldsMap;

public interface LogParser<T> {
    LogTypeInfo<T> inferTypes(String log);
    FieldsMap parse(String log);
}
