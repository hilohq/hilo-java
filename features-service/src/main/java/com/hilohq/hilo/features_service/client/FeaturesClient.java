package com.hilohq.hilo.features_service.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.protobufs.structure.feature.Api;
import com.hilohq.hilo.protobufs.structure.feature.FeaturesGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.*;

public final class FeaturesClient {
  private final FeaturesGrpc.FeaturesFutureStub stub;
  private final ExecutorService executor;

  public FeaturesClient(final Channel channel) {
    stub = FeaturesGrpc.newFutureStub(channel);
    executor = Executors.newSingleThreadExecutor();
  }

  public FeaturesClient() {
    this(HiloConfig.fromEnvironment());
  }

  /** Connects to {@code HILO_FEATURES_SERVICE_HOST}:{@code HILO_FEATURES_SERVICE_PORT}. */
  public FeaturesClient(final HiloConfig config) {
    this(
        ManagedChannelBuilder.forAddress(
                config.get(HiloConfig.Setting.FEATURES_SERVICE_HOST),
                config.getInt(HiloConfig.Setting.FEATURES_SERVICE_PORT))
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

  public CompletableFuture<Api.GetFeatureLogsResponse> getFeaturesLogs(
      final Api.GetFeatureLogsRequest request) {
    return toCompletableFuture(stub.getFeatureLogs(request), executor);
  }

  public CompletableFuture<Api.GetFeaturesResponse> getFeatures(
      final Api.GetFeaturesRequest request) {
    return toCompletableFuture(stub.getFeatures(request), executor);
  }
}
