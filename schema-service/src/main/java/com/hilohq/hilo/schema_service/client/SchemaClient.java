package com.hilohq.hilo.schema_service.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.hilohq.hilo.protobufs.structure.Api;
import com.hilohq.hilo.protobufs.structure.SchemaGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.*;

public final class SchemaClient {
  private final SchemaGrpc.SchemaFutureStub stub;
  private final ExecutorService executor;

  public SchemaClient(final Channel channel) {
    stub = SchemaGrpc.newFutureStub(channel);
    executor = Executors.newSingleThreadExecutor();
  }

  public SchemaClient() {
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

  public CompletableFuture<Api.GetSchemaIdsBySourceResponse> getSchemaIdsBySource(
      final Api.GetSchemaIdsBySourceRequest request) {
    return toCompletableFuture(stub.getSchemaIdsBySource(request), executor);
  }

  public CompletableFuture<Api.GetSchemaLogsResponse> getSchemaLogs(
      final Api.GetSchemaLogsRequest request) {
    return toCompletableFuture(stub.getSchemaLogs(request), executor);
  }

  public CompletableFuture<Api.GetSchemaResponse> getSchema(final Api.GetSchemaRequest request) {
    return toCompletableFuture(stub.getSchema(request), executor);
  }
}
