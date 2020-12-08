package com.hilohq.hilo.connectors_mongo.output;

import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import java.util.concurrent.CompletableFuture;
import org.bson.Document;

public final class MongoInsert implements MongoCommand {

  private final String database;
  private final String collectionName;
  private final Document document;

  public MongoInsert(final String database, final String collectionName, final Document document) {
    this.database = database;
    this.collectionName = collectionName;
    this.document = document;
  }

  @Override
  public CompletableFuture<Void> execute(final MongoClient client) {
    return client.insertOne(database, collectionName, document);
  }
}
