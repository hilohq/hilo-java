package com.hilohq.hilo.parsers.logparse.json;

import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.parsers.logparse.log.LogParser;
import com.hilohq.hilo.parsers.logparse.log.LogTypeInfo;

public final class JsonParser implements LogParser {
    public JsonParser() {

    }

    @Override
    public LogTypeInfo inferTypes(final String log) {
        return null;
    }

    @Override
    public FieldsMap parse(final String log) {
        return null;
    }
}
