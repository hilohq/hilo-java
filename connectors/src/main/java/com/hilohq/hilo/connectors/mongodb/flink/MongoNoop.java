package com.hilohq.hilo.connectors.mongodb.flink;

import com.hilohq.hilo.connectors.mongodb.client.MongoClient;
import java.util.concurrent.CompletableFuture;

public final class MongoNoop implements MongoCommand {
  @Override
  public CompletableFuture<Void> execute(final MongoClient client) {
    final var future = new CompletableFuture<Void>();
    future.complete(null);
    return future;
  }
}
