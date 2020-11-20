package com.hilohq.hilo.features_service.service;

import com.hilohq.hilo.protobufs.structure.feature.Api;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.hilohq.hilo.protobufs.structure.feature.FeaturesGrpc;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FeaturesService extends FeaturesGrpc.FeaturesImplBase {

  private final FeaturesStore store;

  public FeaturesService(final FeaturesStore store) {
    this.store = store;
  }

  private <V, R> void handle(
      final CompletableFuture<V> futureValue,
      final StreamObserver<R> responseObserver,
      final Function<V, R> mapper) {
    futureValue
        .thenAccept(
            value -> {
              final var response = mapper.apply(value);
              responseObserver.onNext(response);
              responseObserver.onCompleted();
            })
        .exceptionally(
            t -> {
              responseObserver.onError(t);
              return null;
            });
  }

  @Override
  public void getFeatures(
      final Api.GetFeaturesRequest request,
      final StreamObserver<Api.GetFeaturesResponse> responseObserver) {
    handle(
        store.getFeatures(request.getSchemaId()),
        responseObserver,
        features ->
            Api.GetFeaturesResponse.newBuilder().putAllFeatures(features.getFeaturesMap()).build());
  }

  @Override
  public void getFeatureLogs(
      final Api.GetFeatureLogsRequest request,
      final StreamObserver<Api.GetFeatureLogsResponse> responseObserver) {
    handle(
        store.getFeatureLogs(request.getSchemaId()),
        responseObserver,
        logs ->
            Api.GetFeatureLogsResponse.newBuilder()
                .addAllLogs(
                    logs.stream()
                        .map(FeatureOuterClass.FeatureLogsModel::getLogs)
                        .collect(Collectors.toList()))
                .build());
  }
}
