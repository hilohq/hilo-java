package com.hilohq.hilo.connector.flink.mongodb;

import com.mongodb.client.MongoDatabase;

public final class MongoNoop<T> implements MongoCommand<T> {
  @Override
  public void execute(final MongoDatabase conn, final T value) {}
}
