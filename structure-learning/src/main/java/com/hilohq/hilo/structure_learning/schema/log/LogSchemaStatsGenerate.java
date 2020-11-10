package com.hilohq.hilo.structure_learning.schema.log;

import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

public final class LogSchemaStatsGenerate extends RichGroupReduceFunction<String, Object> {
    @Override
    public void reduce(final Iterable<String> values, final Collector<Object> out) throws Exception {
        for (final var value : values) {

        }
    }
}
