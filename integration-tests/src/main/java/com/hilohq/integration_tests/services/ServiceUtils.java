package com.hilohq.integration_tests.services;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.features_service.client.FeaturesClient;
import com.hilohq.hilo.features_service.service.FeaturesService;
import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import com.hilohq.hilo.integrations_service.client.IntegrationsClient;
import com.hilohq.hilo.integrations_service.service.IntegrationsService;
import com.hilohq.hilo.integrations_service.service.MongoIntegrationsStore;
import com.hilohq.hilo.schema_service.client.SchemaClient;
import com.hilohq.hilo.schema_service.service.MongoSchemaStore;
import com.hilohq.hilo.schema_service.service.SchemaService;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

public final class ServiceUtils {

  private static final HiloConfig CONFIG = HiloConfig.fromEnvironment();

  private ServiceUtils() {}

  public static ClientServer<SchemaClient, Server> createSchemaService(
      final GrpcCleanupRule grpcCleanupRule) {
    final var store = MongoSchemaStore.open(CONFIG.mongoUrl(), CONFIG.mongoDatabase());
    final var service = new SchemaService(store);
    final var channel = InProcessChannelBuilder.forName("schema").directExecutor().build();
    final var client = new SchemaClient(channel);
    final var server =
        InProcessServerBuilder.forName("schema").directExecutor().addService(service).build();
    grpcCleanupRule.register(server);
    grpcCleanupRule.register(channel);

    return new ClientServer<>(client, server);
  }

  public static ClientServer<FeaturesClient, Server> createFeaturesService(
      final GrpcCleanupRule grpcCleanupRule) {
    final var store = MongoFeaturesStore.open(CONFIG.mongoUrl(), CONFIG.mongoDatabase());
    final var service = new FeaturesService(store);
    final var channel = InProcessChannelBuilder.forName("schema").directExecutor().build();
    final var client = new FeaturesClient(channel);
    final var server =
        InProcessServerBuilder.forName("schema").directExecutor().addService(service).build();
    grpcCleanupRule.register(server);
    grpcCleanupRule.register(channel);

    return new ClientServer<>(client, server);
  }

  public static ClientServer<IntegrationsClient, Server> createIntegrationsService(
      final GrpcCleanupRule grpcCleanupRule) {
    final var store = MongoIntegrationsStore.open(CONFIG.mongoUrl(), CONFIG.mongoDatabase());
    final var service = new IntegrationsService(store);
    final var channel = InProcessChannelBuilder.forName("schema").directExecutor().build();
    final var client = new IntegrationsClient(channel);
    final var server =
        InProcessServerBuilder.forName("schema").directExecutor().addService(service).build();
    grpcCleanupRule.register(server);
    grpcCleanupRule.register(channel);

    return new ClientServer<>(client, server);
  }
}
