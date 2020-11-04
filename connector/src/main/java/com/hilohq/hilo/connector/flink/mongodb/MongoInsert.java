package com.hilohq.hilo.connector.flink.mongodb;

import com.mongodb.client.MongoDatabase;

public final class MongoInsert<T> implements MongoCommand<T> {

  private final String collectionName;
  private final MongoIntoDocumentSerializer<T> serializer;

  public MongoInsert(final String collectionName, final MongoIntoDocumentSerializer<T> serializer) {
    this.collectionName = collectionName;
    this.serializer = serializer;
  }

  @Override
  public void execute(final MongoDatabase conn, final T value) {
    final var collection = conn.getCollection(collectionName);
    collection.insertOne(serializer.serialize(value));
  }
}
