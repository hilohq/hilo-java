package com.hilohq.hilo.pipelines;

import org.apache.flink.api.java.ExecutionEnvironment;

public interface BatchPipeline {
  void run(ExecutionEnvironment executionEnvironment) throws Exception;

  String runAsync(ExecutionEnvironment executionEnvironment) throws Exception;
}
