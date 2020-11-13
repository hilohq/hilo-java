package com.hilohq.integration_tests.pipelines;

import com.hilohq.integration_tests.jars.JarUtils;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.ExecutionEnvironment;

public final class PipelineExecutor<O> {

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

    public PipelineExecutor<O> build() {
      return new PipelineExecutor<>(jarPath, outputFormat);
    }
  }

  public static <O> Builder<O> newBuilder(final OutputFormat<O> outputFormat) {
    return new Builder<>(outputFormat);
  }

  private final String jarPath;
  private final OutputFormat<O> outputFormat;

  public PipelineExecutor(final String jarPath, final OutputFormat<O> outputFormat) {
    this.jarPath = jarPath;
    this.outputFormat = outputFormat;
  }

  public void execute(final String filepath, final PipelineBuilder<String, O> builder)
      throws Exception {
    final var executionEnvironment =
        ExecutionEnvironment.createRemoteEnvironment("127.0.0.1", 8081, jarPath);
    final var source = executionEnvironment.readTextFile(filepath);
    builder.build(executionEnvironment, source).output(outputFormat);
    executionEnvironment.execute();
  }
}
