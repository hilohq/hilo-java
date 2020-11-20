package com.hilohq.hilo.pipelines;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@FunctionalInterface
public interface StreamPipelineBuilder<I, O> {
  DataStream<O> build(StreamExecutionEnvironment executionEnvironment, DataStreamSource<I> input)
      throws Exception;
}
