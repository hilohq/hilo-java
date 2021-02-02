package com.hilohq.hilo.integrations_service.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.hilohq.hilo.protobufs.integrations.Api;
import com.hilohq.hilo.protobufs.integrations.IntegrationsGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.*;

public final class IntegrationsClient {
  private final IntegrationsGrpc.IntegrationsFutureStub stub;
  private final ExecutorService executor;

  public IntegrationsClient(final Channel channel) {
    stub = IntegrationsGrpc.newFutureStub(channel);
    executor = Executors.newSingleThreadExecutor();
  }

  public IntegrationsClient() {
    this(ManagedChannelBuilder.forAddress("localhost", 1235).usePlaintext().build());
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

  public CompletableFuture<Api.GetPrometheusMetricsResponse> getPrometheusMetrics(
      final Api.GetPrometheusMetricsRequest request) {
    return toCompletableFuture(stub.getPrometheusMetrics(request), executor);
  }

  public CompletableFuture<Api.GetJdbcMetricsResponse> getPrometheusMetrics(
      final Api.GetJdbcMetricsRequest request) {
    return toCompletableFuture(stub.getJdbcMetrics(request), executor);
  }
}
