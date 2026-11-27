package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.jars.JarUtils;
import org.apache.flink.api.java.ExecutionEnvironment;

public final class DefaultBatchExecutor implements BatchExecutor, AsyncBatchExecutor {
  public static final class Builder {
    private String jarPath;
    private final HiloConfig config = HiloConfig.fromEnvironment();
    private String remoteHost = config.flinkHost();
    private int remotePort = config.flinkPort();

    public Builder jarPath(final String jarPath) {
      verifyNotNull(jarPath);
      this.jarPath = jarPath;
      return this;
    }

    public Builder jarModule(final String module) {
      verifyNotNull(module);
      return jarPath(JarUtils.jarPathForModule(module).toString());
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

    public DefaultBatchExecutor build() {
      verifyNotNull(jarPath);
      verifyNotNull(remoteHost);

      return new DefaultBatchExecutor(jarPath, remoteHost, remotePort);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private final String jarPath;
  private final String remoteHost;
  private final int remotePort;

  DefaultBatchExecutor(final String jarPath, final String remoteHost, final int remotePort) {
    this.jarPath = jarPath;
    this.remotePort = remotePort;
    this.remoteHost = remoteHost;
  }

  @Override
  public void execute(final BatchPipeline pipeline) throws Exception {
    final var executionEnvironment =
        ExecutionEnvironment.createRemoteEnvironment(remoteHost, remotePort, jarPath);
    pipeline.run(executionEnvironment);
  }

  @Override
  public String executeAsync(final BatchPipeline pipeline) throws Exception {
    final var executionEnvironment =
        ExecutionEnvironment.createRemoteEnvironment(remoteHost, remotePort, jarPath);
    return pipeline.runAsync(executionEnvironment);
  }
}
