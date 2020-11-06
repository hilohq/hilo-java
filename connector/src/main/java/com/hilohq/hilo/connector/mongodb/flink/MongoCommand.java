package com.hilohq.hilo.connector.mongodb.flink;

import com.hilohq.hilo.connector.mongodb.client.MongoClient;

@FunctionalInterface
public interface MongoCommand<T> {
  void execute(MongoClient conn, T value);
}
