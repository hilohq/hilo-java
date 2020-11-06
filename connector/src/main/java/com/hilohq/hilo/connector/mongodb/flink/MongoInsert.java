package com.hilohq.hilo.connector.mongodb.flink;

import com.hilohq.hilo.connector.mongodb.client.MongoClient;

public final class MongoInsert<T> implements MongoCommand<T> {

  private final String database;
  private final String collectionName;
  private final MongoIntoDocumentSerializer<T> serializer;

  public MongoInsert(
      final String database,
      final String collectionName,
      final MongoIntoDocumentSerializer<T> serializer) {
    this.database = database;
    this.collectionName = collectionName;
    this.serializer = serializer;
  }

  @Override
  public void execute(final MongoClient client, final T value) {
    client.insertOne(database, collectionName, serializer.serialize(value));
  }
}
