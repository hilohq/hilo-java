package com.hilohq.hilo.features_server;

import com.hilohq.hilo.features_service.service.FeaturesService;
import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import io.grpc.ServerBuilder;

public final class FeaturesServer {
  public static void main(final String[] args) throws Exception {
    final var store = MongoFeaturesStore.open("mongodb://root:example@localhost:27017/admin");
    final var service = new FeaturesService(store);
    final var server = ServerBuilder.forPort(1235).addService(service).build();
    server.start();
    server.awaitTermination();
  }
}
