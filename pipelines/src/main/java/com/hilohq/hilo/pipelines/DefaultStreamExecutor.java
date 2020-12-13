package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.jars.JarUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public final class DefaultStreamExecutor implements StreamExecutor, AsyncStreamExecutor {
  public static final class Builder {
    private String jarPath;
    private String remoteHost = "127.0.0.1";
    private int remotePort = 8081;

    public Builder jarPath(final String jarPath) {
      verifyNotNull(jarPath);
      this.jarPath = jarPath;
      return this;
    }

    public Builder jarModule(final String module) {
      verifyNotNull(module);
      jarPath = JarUtils.jarPathForModule(module).toString();
      return this;
    }

    public Builder remoteHost(final String remoteHost) {
      verifyNotNull(remoteHost);
      this.remoteHost = remoteHost;
      return this;
    }

    public Builder remotePort(final int remotePort) {
      this.remotePort = remotePort;
      return this;
    }

    public DefaultStreamExecutor build() {
      verifyNotNull(jarPath);
      verifyNotNull(remoteHost);

      return new DefaultStreamExecutor(jarPath, remoteHost, remotePort);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private final String jarPath;
  private final String remoteHost;
  private final int remotePort;

  DefaultStreamExecutor(final String jarPath, final String remoteHost, final int remotePort) {
    this.jarPath = jarPath;
    this.remotePort = remotePort;
    this.remoteHost = remoteHost;
  }

  @Override
  public void execute(final StreamPipeline pipeline) throws Exception {
    final var executionEnvironment =
        StreamExecutionEnvironment.createRemoteEnvironment(remoteHost, remotePort, jarPath);
    pipeline.run(executionEnvironment);
  }

  @Override
  public String executeAsync(StreamPipeline pipeline) throws Exception {
    final var executionEnvironment =
        StreamExecutionEnvironment.createRemoteEnvironment(remoteHost, remotePort, jarPath);
    return pipeline.runAsync(executionEnvironment);
  }
}
