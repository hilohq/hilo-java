package com.hilohq.hilo.connector.flink.mongodb;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.conversions.Bson;

public final class MongoUpdate<T> implements MongoCommand<T> {

  private final String collectionName;
  private final Bson filter;
  private final MongoIntoDocumentSerializer<T> serializer;

  public MongoUpdate(
      final String collectionName,
      final Bson filter,
      final MongoIntoDocumentSerializer<T> serializer) {
    this.collectionName = collectionName;
    this.filter = filter;
    this.serializer = serializer;
  }

  @Override
  public void execute(final MongoDatabase conn, final T value) {
    final var collection = conn.getCollection(collectionName);
    final var options = new UpdateOptions();
    options.upsert(true);
    collection.updateOne(filter, serializer.serialize(value), options);
  }
}
