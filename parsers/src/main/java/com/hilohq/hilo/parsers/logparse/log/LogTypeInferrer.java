package com.hilohq.hilo.parsers.logparse.log;

@FunctionalInterface
public interface LogTypeInferrer {
    LogTypeInfo inferTypes(String log);
}
