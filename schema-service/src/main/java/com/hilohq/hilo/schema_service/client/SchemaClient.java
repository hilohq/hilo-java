package com.hilohq.hilo.schema_service.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.hilohq.hilo.protobufs.schema.Api;
import com.hilohq.hilo.protobufs.schema.SchemaGrpc;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.*;

public final class SchemaClient {
    private final SchemaGrpc.SchemaFutureStub stub;
    private final ExecutorService executor;

    public SchemaClient() {
        stub = SchemaGrpc.newFutureStub(ManagedChannelBuilder.forAddress("localhost", 1234).usePlaintext().build());
        executor = Executors.newSingleThreadExecutor();
    }

    public void close() {
        executor.shutdown();
    }

    private static <T> CompletableFuture<T> toCompletableFuture(final ListenableFuture<T> future,
                                                                final Executor executor) {
        final var result = new CompletableFuture<T>();
        System.out.println("toCompletableFuture");
        future.addListener(() -> {
            try {
                System.out.println("runnable running");
                final T value = future.get();
                result.complete(value);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("EXCEPTION " + e);
                result.completeExceptionally(e);
            }
        }, executor);
        return result;
    }

    public CompletableFuture<Api.GetSchemaResponse> getSchema(final Api.GetSchemaRequest request) {
        return toCompletableFuture(stub.getSchema(request), executor);
    }

    public CompletableFuture<Api.GetSchemaFieldsResponse> getSchemaFields(final Api.GetSchemaFieldsRequest request) {
        final var future = new CompletableFuture<Api.GetSchemaResponse>();
        return toCompletableFuture(stub.getSchemaFields(request), executor);
    }

    public CompletableFuture<Api.GetSchemaLogsResponse> getSchemaLogs(final Api.GetSchemaLogsRequest request) {
        final var future = new CompletableFuture<Api.GetSchemaResponse>();
        return toCompletableFuture(stub.getSchemaLogs(request), executor);
    }

    public CompletableFuture<Api.GetSchemaParseErrorsResponse> getSchemaParseErrors(final Api.GetSchemaParseErrorsRequest request) {
        final var future = new CompletableFuture<Api.GetSchemaResponse>();
        return toCompletableFuture(stub.getSchemaParseErrors(request), executor);
    }
}
