package com.hilohq.hilo.stages;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.streaming.api.datastream.DataStream;

@FunctionalInterface
public interface StreamStages<I, O> {
  DataStream<O> bind(ExecutionConfig config, DataStream<I> input) throws Exception;
}
