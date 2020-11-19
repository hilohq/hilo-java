package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.jars.JarUtils;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.ExecutionEnvironment;

public final class BatchPipelineExecutor<O> {

  public static class Builder<O> {
    private final OutputFormat<O> outputFormat;
    private String jarPath;

    public Builder(final OutputFormat<O> outputFormat) {
      this.outputFormat = outputFormat;
    }

    public Builder<O> jarModule(final String module) {
      jarPath = JarUtils.jarPathForModule(module).toString();
      return this;
    }

    public BatchPipelineExecutor<O> build() {
      return new BatchPipelineExecutor<>(jarPath, outputFormat);
    }
  }

  public static <O> Builder<O> newBuilder(final OutputFormat<O> outputFormat) {
    return new Builder<>(outputFormat);
  }

  private final String jarPath;
  private final OutputFormat<O> outputFormat;

  public BatchPipelineExecutor(final String jarPath, final OutputFormat<O> outputFormat) {
    this.jarPath = jarPath;
    this.outputFormat = outputFormat;
  }

  public void execute(final String filepath, final BatchPipelineBuilder<String, O> builder)
      throws Exception {
    final var executionEnvironment =
        ExecutionEnvironment.createRemoteEnvironment("127.0.0.1", 8081, jarPath);
    final var source = executionEnvironment.readTextFile(filepath);
    builder.build(executionEnvironment, source).output(outputFormat);
    executionEnvironment.execute();
  }
}
