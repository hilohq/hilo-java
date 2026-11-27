package com.hilohq.hilo.pipelines_service.client;

import static com.hilohq.hilo.protobufs.pipelines.Api.*;

import com.google.common.util.concurrent.ListenableFuture;
import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.protobufs.pipelines.PipelinesGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.*;

public final class PipelinesClient {
  private final PipelinesGrpc.PipelinesFutureStub stub;
  private final ExecutorService executor;

  public PipelinesClient(final Channel channel) {
    stub = PipelinesGrpc.newFutureStub(channel);
    executor = Executors.newSingleThreadExecutor();
  }

  public PipelinesClient() {
    this(HiloConfig.fromEnvironment());
  }

  /** Connects to {@code HILO_PIPELINES_SERVICE_HOST}:{@code HILO_PIPELINES_SERVICE_PORT}. */
  public PipelinesClient(final HiloConfig config) {
    this(
        ManagedChannelBuilder.forAddress(
                config.get(HiloConfig.Setting.PIPELINES_SERVICE_HOST),
                config.getInt(HiloConfig.Setting.PIPELINES_SERVICE_PORT))
            .usePlaintext()
            .build());
  }

  public void close() {
    executor.shutdown();
  }

  private static <T> CompletableFuture<T> toCompletableFuture(
      final ListenableFuture<T> future, final Executor executor) {
    final var result = new CompletableFuture<T>();
    future.addListener(
        () -> {
          try {
            final T value = future.get();
            result.complete(value);
          } catch (InterruptedException | ExecutionException e) {
            result.completeExceptionally(e);
          }
        },
        executor);
    return result;
  }

  public CompletableFuture<ExecuteBatchPipelineResponse> executeBatchPipeline(
      final ExecuteBatchPipelineRequest request) {
    return toCompletableFuture(stub.executeBatchPipeline(request), executor);
  }
}
