package com.hilohq.hilo.connector.mongodb.flink;

import com.hilohq.hilo.connector.mongodb.client.MongoClient;
import com.mongodb.client.model.UpdateOptions;
import org.bson.conversions.Bson;

public final class MongoUpdate<T> implements MongoCommand<T> {

  private final String database;
  private final String collectionName;
  private final Bson filter;
  private final MongoIntoDocumentSerializer<T> serializer;

  public MongoUpdate(
      final String database,
      final String collectionName,
      final Bson filter,
      final MongoIntoDocumentSerializer<T> serializer) {
    this.database = database;
    this.collectionName = collectionName;
    this.filter = filter;
    this.serializer = serializer;
  }

  @Override
  public void execute(final MongoClient client, final T value) {
    final var options = new UpdateOptions();
    options.upsert(true);
    client.updateOne(database, collectionName, filter, serializer.serialize(value), options);
  }
}
