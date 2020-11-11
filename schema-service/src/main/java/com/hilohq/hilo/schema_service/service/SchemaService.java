package com.hilohq.hilo.schema_service.service;

import static com.hilohq.hilo.protobufs.structure.Api.*;

import com.hilohq.hilo.protobufs.structure.Schema;
import com.hilohq.hilo.protobufs.structure.SchemaGrpc.SchemaImplBase;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SchemaService extends SchemaImplBase {

  private final SchemaStore store;

  public SchemaService(final SchemaStore store) {
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
  public void getSchema(
      final GetSchemaRequest request, final StreamObserver<GetSchemaResponse> responseObserver) {
    handle(
        store.getSchema(request.getSchemaId()),
        responseObserver,
        schema -> {
          final var responseBuilder = GetSchemaResponse.newBuilder()
                  .setSchemaId(schema.getMetadata().getSchemaId());
          if (schema.hasJson()) {
            responseBuilder.setJson(schema.getJson());
          } else if (schema.hasMysql()) {
            responseBuilder.setMysql(schema.getMysql());
          } else if (schema.hasText()) {
            responseBuilder.setText(schema.getText());
          }

          return responseBuilder.build();
        });
  }

  @Override
  public void getSchemaIdsBySource(
      final GetSchemaIdsBySourceRequest request,
      final StreamObserver<GetSchemaIdsBySourceResponse> responseObserver) {
    handle(
        store.getSchemaIdsBySource(request.getSourceId()),
        responseObserver,
        metadataModels -> GetSchemaIdsBySourceResponse.newBuilder().addAllSchemaId(metadataModels.stream().map(metadata ->
          metadata.getMetadata().getSchemaId()).collect(Collectors.toList())).build());
  }

  @Override
  public void getSchemaLogs(
      final GetSchemaLogsRequest request,
      final StreamObserver<GetSchemaLogsResponse> responseObserver) {
    handle(
        store.getSchemaLogs(request.getSchemaId()),
        responseObserver, SchemaService::createSchemaLogsResponse);
  }

  private static GetSchemaLogsResponse createSchemaLogsResponse(final Collection<Schema.SchemaLogsModel> logs) {
    if (logs.isEmpty()) {
      return GetSchemaLogsResponse.newBuilder().build();
    }

    final var responseBuilder = GetSchemaLogsResponse.newBuilder();
    final var item = logs.iterator().next();
    if (item.hasJson()) {
      return responseBuilder.setJson(JsonLogs.newBuilder().addAllLogs(logs.stream().map(Schema.SchemaLogsModel::getJson).collect(Collectors.toList()))).build();

    } else if (item.hasMysql()) {
      throw new NotImplementedException("mysql not implemented");

    } else if (item.hasText()) {
      throw new NotImplementedException("text not implemented");
    }

    return responseBuilder.build();
  }
}
