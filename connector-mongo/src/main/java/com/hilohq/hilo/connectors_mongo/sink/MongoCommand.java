package com.hilohq.hilo.connectors_mongo.sink;

import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface MongoCommand {
  CompletableFuture<Void> execute(MongoClient conn);
}
