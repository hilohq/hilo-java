package com.hilohq.hilo.pipelines;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;

@FunctionalInterface
public interface BatchPipelineBuilder<I, O> {
  DataSet<O> build(ExecutionEnvironment executionEnvironment, DataSource<I> input) throws Exception;
}
