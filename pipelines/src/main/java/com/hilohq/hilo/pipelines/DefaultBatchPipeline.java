package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.stages.BatchStages;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.core.io.InputSplit;

public class DefaultBatchPipeline<I, S extends InputSplit, O> implements BatchPipeline {
  private final InputFormat<I, S> input;
  private final BatchStages<I, O> stages;
  private final OutputFormat<O> output;
  private final TypeInformationSupplier<I> typeInformationSupplier;

  DefaultBatchPipeline(
      final InputFormat<I, S> input, final BatchStages<I, O> stages, final OutputFormat<O> output) {
    this(input, stages, output, new DefaultTypeInformationSupplier<>());
  }

  DefaultBatchPipeline(
      final InputFormat<I, S> input,
      final BatchStages<I, O> stages,
      final OutputFormat<O> output,
      final TypeInformationSupplier<I> typeInformationSupplier) {
    this.input = input;
    this.stages = stages;
    this.output = output;
    this.typeInformationSupplier = typeInformationSupplier;
  }

  @Override
  public void run(final ExecutionEnvironment executionEnvironment) throws Exception {
    final var typeInformation = typeInformationSupplier.of(input);
    final var source = executionEnvironment.createInput(input, typeInformation);
    stages.bind(executionEnvironment.getConfig(), source).output(output);
    executionEnvironment.execute();
  }
}
