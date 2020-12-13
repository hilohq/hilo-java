package com.hilohq.hilo.pipelines;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public interface StreamPipeline {
  void run(StreamExecutionEnvironment executionEnvironment) throws Exception;

  String runAsync(StreamExecutionEnvironment executionEnvironment) throws Exception;
}
