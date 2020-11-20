package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.stages.StreamStages;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class DefaultStreamPipeline<I, O> implements StreamPipeline {
  private final FileInputFormat<I> input;
  private final StreamStages<I, O> stages;
  private final SinkFunction<O> sink;

  DefaultStreamPipeline(
      final FileInputFormat<I> input, final StreamStages<I, O> stages, final SinkFunction<O> sink) {
    this.input = input;
    this.stages = stages;
    this.sink = sink;
  }

  @Override
  public void run(final StreamExecutionEnvironment executionEnvironment) throws Exception {
    final var source = executionEnvironment.createInput(input);
    stages.bind(executionEnvironment.getConfig(), source).addSink(sink);
    executionEnvironment.execute();
  }
}
