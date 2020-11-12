package com.hilohq.integration_tests.pipelines;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSource;

@FunctionalInterface
public interface PipelineBuilder<I, O> {
  DataSet<O> build(DataSource<I> input);
}
