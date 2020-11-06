package com.hilohq.hilo.connector.mongodb.client;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.Success;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.bson.Document;
import org.bson.conversions.Bson;

public final class MongoClient {
  private final com.mongodb.reactivestreams.client.MongoClient client;

  MongoClient(final com.mongodb.reactivestreams.client.MongoClient client) {
    this.client = client;
  }

  public CompletableFuture<Document> findOne(
      final String database, final String collectionName, final Bson filter) {
    final var future = new CompletableFuture<Document>();
    final var subscriber = new FindSingleResultSubscriber<>(future);
    final var publisher = client.getDatabase(database).getCollection(collectionName).find(filter);
    publisher.subscribe(subscriber);
    return future;
  }

  public <T> CompletableFuture<T> findOne(
      final String database, final String collectionName, final Bson filter, final Class<T> type) {
    final var future = new CompletableFuture<T>();
    final var subscriber = new FindSingleResultSubscriber<>(future);
    final var publisher =
        client.getDatabase(database).getCollection(collectionName).find(filter, type);
    publisher.subscribe(subscriber);
    return future;
  }

  public CompletableFuture<Collection<Document>> find(
      final String database, final String collectionName, final Bson filter) {
    final var future = new CompletableFuture<Collection<Document>>();
    final var subscriber = new FindResultSubscriber<>(future);
    final var publisher = client.getDatabase(database).getCollection(collectionName).find(filter);
    publisher.subscribe(subscriber);
    return future;
  }

  public <T> CompletableFuture<Collection<T>> find(
      final String database, final String collectionName, final Bson filter, final Class<T> type) {
    final var future = new CompletableFuture<Collection<T>>();
    final var subscriber = new FindResultSubscriber<>(future);
    final var publisher =
        client.getDatabase(database).getCollection(collectionName).find(filter, type);
    publisher.subscribe(subscriber);
    return future;
  }

  public CompletableFuture<Void> insertOne(
      final String database, final String collectionName, final Document document) {
    final var future = new CompletableFuture<Void>();
    final var subscriber = new ResultSubscriber<Success>(future);
    final var publisher =
        client.getDatabase(database).getCollection(collectionName).insertOne(document);
    publisher.subscribe(subscriber);
    return future;
  }

  public CompletableFuture<Void> updateOne(
      final String database,
      final String collectionName,
      final Bson filter,
      final Document document,
      final UpdateOptions options) {
    System.out.println("UPDATE ONE ");
    final var future = new CompletableFuture<Void>();
    final var subscriber = new ResultSubscriber<UpdateResult>(future);
    final var publisher =
        client
            .getDatabase(database)
            .getCollection(collectionName)
            .updateOne(filter, document, options);
    publisher.subscribe(subscriber);
    return future;
  }

  public void close() {
    client.close();
  }
}
