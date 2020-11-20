package com.hilohq.hilo.stages;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.DataSet;

@FunctionalInterface
public interface BatchStages<I, O> {
  DataSet<O> bind(ExecutionConfig config, DataSet<I> input) throws Exception;
}
