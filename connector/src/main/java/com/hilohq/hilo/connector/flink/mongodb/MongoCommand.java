package com.hilohq.hilo.connector.flink.mongodb;

import com.mongodb.client.MongoDatabase;

@FunctionalInterface
public interface MongoCommand<T> {
  void execute(MongoDatabase conn, T value);
}
