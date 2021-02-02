package com.hilohq.hilo.integrations_service.service;

import com.hilohq.hilo.protobufs.integrations.Api;
import com.hilohq.hilo.protobufs.integrations.IntegrationsGrpc;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class IntegrationsService extends IntegrationsGrpc.IntegrationsImplBase {

  private final IntegrationsStore store;

  public IntegrationsService(final IntegrationsStore store) {
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
  public void getPrometheusMetrics(
      final Api.GetPrometheusMetricsRequest request,
      final StreamObserver<Api.GetPrometheusMetricsResponse> responseObserver) {
    handle(
        store.getPrometheusMetrics(request.getSchemaId()),
        responseObserver,
        metrics ->
            Api.GetPrometheusMetricsResponse.newBuilder()
                .putAllMetrics(metrics.getMetricsMap())
                .build());
  }

  @Override
  public void getJdbcMetrics(
      final Api.GetJdbcMetricsRequest request,
      final StreamObserver<Api.GetJdbcMetricsResponse> responseObserver) {
    handle(
        store.getJdbcMetrics(request.getSchemaId()),
        responseObserver,
        metrics -> Api.GetJdbcMetricsResponse.newBuilder().setMysql(metrics.getMysql()).build());
  }
}
