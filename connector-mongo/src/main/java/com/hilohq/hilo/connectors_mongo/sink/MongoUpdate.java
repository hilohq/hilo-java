package com.hilohq.hilo.connectors_mongo.sink;

import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import com.mongodb.client.model.UpdateOptions;
import java.util.concurrent.CompletableFuture;
import org.bson.Document;
import org.bson.conversions.Bson;

public final class MongoUpdate implements MongoCommand {

  private final String database;
  private final String collectionName;
  private final Bson filter;
  private final Document document;

  public MongoUpdate(
      final String database,
      final String collectionName,
      final Bson filter,
      final Document document) {
    this.database = database;
    this.collectionName = collectionName;
    this.filter = filter;
    this.document = document;
  }

  @Override
  public CompletableFuture<Void> execute(final MongoClient client) {
    final var options = new UpdateOptions();
    options.upsert(true);
    return client.updateOne(database, collectionName, filter, document, options);
  }
}
