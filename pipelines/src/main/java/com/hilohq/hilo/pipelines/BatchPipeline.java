package com.hilohq.hilo.pipelines;

import org.apache.flink.api.java.ExecutionEnvironment;

@FunctionalInterface
public interface BatchPipeline {
  void run(ExecutionEnvironment executionEnvironment) throws Exception;
}
