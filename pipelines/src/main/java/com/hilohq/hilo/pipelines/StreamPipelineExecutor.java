package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.jars.JarUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public final class StreamPipelineExecutor<O> {

  public static class Builder<O> {
    private final SinkFunction<O> sink;
    private String jarPath;

    public Builder(final SinkFunction<O> sink) {
      this.sink = sink;
    }

    public Builder<O> jarModule(final String module) {
      jarPath = JarUtils.jarPathForModule(module).toString();
      return this;
    }

    public StreamPipelineExecutor<O> build() {
      return new StreamPipelineExecutor<>(jarPath, sink);
    }
  }

  public static <O> Builder<O> newBuilder(final SinkFunction<O> sink) {
    return new Builder<>(sink);
  }

  private final String jarPath;
  private final SinkFunction<O> sink;

  public StreamPipelineExecutor(final String jarPath, final SinkFunction<O> sink) {
    this.jarPath = jarPath;
    this.sink = sink;
  }

  public void execute(final String filepath, final StreamPipelineBuilder<String, O> builder)
      throws Exception {
    final var executionEnvironment =
        StreamExecutionEnvironment.createRemoteEnvironment("127.0.0.1", 8081, jarPath);
    final var source = executionEnvironment.readTextFile(filepath);
    builder.build(executionEnvironment, source).addSink(sink);
    executionEnvironment.execute();
  }
}
