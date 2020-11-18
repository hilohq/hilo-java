package com.hilohq.hilo.connectors_mongo.client;

import com.mongodb.MongoClientSettings;

public final class MongoClients {

  private MongoClients() {}

  public static MongoClient create(final String url) {
    final var client = com.mongodb.reactivestreams.client.MongoClients.create(url);
    return new MongoClient(client);
  }

  public static MongoClient create(final MongoClientSettings settings) {
    final var client = com.mongodb.reactivestreams.client.MongoClients.create(settings);
    return new MongoClient(client);
  }
}
