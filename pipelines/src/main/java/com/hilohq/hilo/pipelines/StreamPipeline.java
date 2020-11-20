package com.hilohq.hilo.pipelines;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@FunctionalInterface
public interface StreamPipeline {
  void run(StreamExecutionEnvironment executionEnvironment) throws Exception;
}
