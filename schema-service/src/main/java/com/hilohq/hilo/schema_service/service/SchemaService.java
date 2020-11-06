package com.hilohq.hilo.schema_service.service;

import static com.hilohq.hilo.protobufs.schema.Api.*;

import com.hilohq.hilo.protobufs.schema.Model;
import com.hilohq.hilo.protobufs.schema.SchemaGrpc.SchemaImplBase;
import io.grpc.stub.StreamObserver;
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
        store.getSchemaMetadata(request.getSourceId()),
        responseObserver,
        metadata ->
            GetSchemaResponse.newBuilder()
                .addAllSchemas(
                    metadata
                        .stream()
                        .map(Model.SchemaMetadataModel::getMetadata)
                        .collect(Collectors.toList()))
                .build());
  }

  @Override
  public void getSchemaFields(
      final GetSchemaFieldsRequest request,
      final StreamObserver<GetSchemaFieldsResponse> responseObserver) {
    handle(
        store.getSchema(request.getSchemaId()),
        responseObserver,
        schema -> GetSchemaFieldsResponse.newBuilder().setSchema(schema).build());
  }

  @Override
  public void getSchemaLogs(
      final GetSchemaLogsRequest request,
      final StreamObserver<GetSchemaLogsResponse> responseObserver) {
    handle(
        store.getSchemaLogs(request.getSchemaId()),
        responseObserver,
        logs -> GetSchemaLogsResponse.newBuilder().addAllLogs(logs).build());
  }

  @Override
  public void getSchemaParseErrors(
      final GetSchemaParseErrorsRequest request,
      final StreamObserver<GetSchemaParseErrorsResponse> responseObserver) {
    handle(
        store.getSchemaErrors(request.getSchemaId()),
        responseObserver,
        errors -> GetSchemaParseErrorsResponse.newBuilder().addAllErrors(errors).build());
  }
}
