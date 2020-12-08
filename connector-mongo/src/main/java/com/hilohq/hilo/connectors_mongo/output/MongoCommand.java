package com.hilohq.hilo.connectors_mongo.output;

import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface MongoCommand {
  CompletableFuture<Void> execute(MongoClient conn);
}
