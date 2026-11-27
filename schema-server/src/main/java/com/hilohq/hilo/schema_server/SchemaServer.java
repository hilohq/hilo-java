package com.hilohq.hilo.schema_server;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.schema_service.service.MongoSchemaStore;
import com.hilohq.hilo.schema_service.service.SchemaService;
import io.grpc.ServerBuilder;

public final class SchemaServer {
  public static void main(final String[] args) throws Exception {
    final var config = HiloConfig.fromEnvironment();
    final var store = MongoSchemaStore.open(config.mongoUrl(), config.mongoDatabase());
    final var service = new SchemaService(store);
    final var server =
        ServerBuilder.forPort(config.getInt(HiloConfig.Setting.SCHEMA_SERVICE_PORT))
            .addService(service)
            .build();
    server.start();
    server.awaitTermination();
  }
}
