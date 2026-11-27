package com.hilohq.hilo.features_server;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.features_service.service.FeaturesService;
import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import io.grpc.ServerBuilder;

public final class FeaturesServer {
  public static void main(final String[] args) throws Exception {
    final var config = HiloConfig.fromEnvironment();
    final var store = MongoFeaturesStore.open(config.mongoUrl(), config.mongoDatabase());
    final var service = new FeaturesService(store);
    final var server =
        ServerBuilder.forPort(config.getInt(HiloConfig.Setting.FEATURES_SERVICE_PORT))
            .addService(service)
            .build();
    server.start();
    server.awaitTermination();
  }
}
