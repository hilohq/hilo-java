package com.hilohq.hilo.pipelines_service.service;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.pipelines.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;

public final class FlinkClient {
  private final AsyncBatchExecutor batchExecutor;
  private final AsyncStreamExecutor streamExecutor;
  private final RestClusterClient<StandaloneClusterId> restClient;

  public FlinkClient() {
    this(HiloConfig.fromEnvironment());
  }

  /** Talks to the JobManager at {@code HILO_FLINK_HOST}:{@code HILO_FLINK_PORT}. */
  public FlinkClient(final HiloConfig config) {
    this(config.flinkHost(), config.flinkPort());
  }

  public FlinkClient(final String host, final int port) {
    this.batchExecutor =
        DefaultBatchExecutor.newBuilder()
            .jarModule("hilo-tool")
            .remoteHost(host)
            .remotePort(port)
            .build();
    this.streamExecutor =
        DefaultStreamExecutor.newBuilder()
            .jarModule("hilo-tool")
            .remoteHost(host)
            .remotePort(port)
            .build();
    this.restClient = createRestClient(host, port);
  }

  private static RestClusterClient<StandaloneClusterId> createRestClient(
      final String host, final int port) {
    try {
      final var clusterId = StandaloneClusterId.getInstance();
      final var configuration = new Configuration();
      configuration.setString(JobManagerOptions.ADDRESS, host);
      configuration.setInteger(JobManagerOptions.PORT, port);
      return new RestClusterClient<>(configuration, clusterId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static JobID jobIdFromHexString(final String hex) {
    try {
      return JobID.fromHexString(hex);
    } catch (IllegalArgumentException e) {
      throw new StatusRuntimeException(Status.INVALID_ARGUMENT);
    }
  }

  public CompletableFuture<String> executeBatchPipeline(final BatchPipeline pipeline) {
    try {
      return CompletableFuture.completedFuture(batchExecutor.executeAsync(pipeline));
    } catch (Exception e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  public CompletableFuture<String> executeStreamPipeline(final StreamPipeline pipeline) {
    try {
      return CompletableFuture.completedFuture(streamExecutor.executeAsync(pipeline));
    } catch (Exception e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  public CompletableFuture<JobStatus> getPipelineStatus(final String jobId) {
    return restClient.getJobStatus(jobIdFromHexString(jobId));
  }

  public CompletableFuture<Void> cancelPipeline(final String jobId) {
    return restClient.cancel(jobIdFromHexString(jobId)).thenAccept(ack -> {});
  }
}
