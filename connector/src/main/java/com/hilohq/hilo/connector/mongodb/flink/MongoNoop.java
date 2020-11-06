package com.hilohq.hilo.connector.mongodb.flink;

import com.hilohq.hilo.connector.mongodb.client.MongoClient;

public final class MongoNoop<T> implements MongoCommand<T> {
  @Override
  public void execute(final MongoClient client, final T value) {}
}
