package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.stages.BatchStages;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.ExecutionEnvironment;

public class DefaultBatchPipeline<I, O> implements BatchPipeline {
  private final FileInputFormat<I> input;
  private final BatchStages<I, O> stages;
  private final OutputFormat<O> output;

  DefaultBatchPipeline(
      final FileInputFormat<I> input,
      final BatchStages<I, O> stages,
      final OutputFormat<O> output) {
    this.input = input;
    this.stages = stages;
    this.output = output;
  }

  @Override
  public void run(final ExecutionEnvironment executionEnvironment) throws Exception {
    final var source = executionEnvironment.createInput(input);
    stages.bind(executionEnvironment.getConfig(), source).output(output);
    executionEnvironment.execute();
  }
}
