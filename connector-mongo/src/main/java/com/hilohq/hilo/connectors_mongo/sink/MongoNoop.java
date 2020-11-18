package com.hilohq.hilo.connectors_mongo.sink;

import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import java.util.concurrent.CompletableFuture;

public final class MongoNoop implements MongoCommand {
  @Override
  public CompletableFuture<Void> execute(final MongoClient client) {
    final var future = new CompletableFuture<Void>();
    future.complete(null);
    return future;
  }
}
