package com.hilohq.hilo.pipelines_service.client;

import static com.hilohq.hilo.protobufs.pipelines.Api.*;

import com.google.common.util.concurrent.ListenableFuture;
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
    this(ManagedChannelBuilder.forAddress("localhost", 1234).usePlaintext().build());
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

  public CompletableFuture<ExecuteBatchPipelineResponse> getSchemaIdsBySource(
      final ExecuteBatchPipelineRequest request) {
    return toCompletableFuture(stub.executeBatchPipeline(request), executor);
  }
}
