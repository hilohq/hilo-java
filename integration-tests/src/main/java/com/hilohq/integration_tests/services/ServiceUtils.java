package com.hilohq.integration_tests.services;

import com.hilohq.hilo.schema_service.client.SchemaClient;
import com.hilohq.hilo.schema_service.service.MongoSchemaStore;
import com.hilohq.hilo.schema_service.service.SchemaService;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

public final class ServiceUtils {

  private ServiceUtils() {}

  public static ClientServer<SchemaClient, Server> createSchemaService(
      final GrpcCleanupRule grpcCleanupRule) {
    final var store = MongoSchemaStore.open("mongodb://root:example@localhost:27017/admin");
    final var service = new SchemaService(store);
    final var channel = InProcessChannelBuilder.forName("schema").directExecutor().build();
    final var client = new SchemaClient(channel);
    final var server =
        InProcessServerBuilder.forName("schema").directExecutor().addService(service).build();
    grpcCleanupRule.register(server);
    grpcCleanupRule.register(channel);

    return new ClientServer<>(client, server);
  }
}
