package com.hilohq.hilo.connectors.mongodb.flink;

import com.hilohq.hilo.connectors.mongodb.client.MongoClient;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface MongoCommand {
  CompletableFuture<Void> execute(MongoClient conn);
}
