package com.hilohq.hilo.hilo_flink.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.nio.charset.StandardCharsets;

import com.mongodb.client.model.UpdateOptions;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bson.Document;

public final class MongoDbSink<T> extends RichSinkFunction<T> {

  private final String mongoUrl;
  private final MongoQueryGenerator<T> queryGenerator;
  private final CollectionSupplier<T> collectionSupplier;
  private final FilterSupplier<T> filterSupplier;
  private final String database;
  private transient MongoClient mongo;

  public MongoDbSink(
      final String mongoUrl,
      final MongoQueryGenerator<T> queryGenerator,
      final String database,
      final String collection,
      final FilterSupplier<T> filterSupplier) {
    this(mongoUrl, queryGenerator, database, new SingleCollectionSupplier<>(collection), filterSupplier);
  }

  public MongoDbSink(
      final String mongoUrl,
      final MongoQueryGenerator<T> queryGenerator,
      final String database,
      final CollectionSupplier<T> collectionSupplier,
      final FilterSupplier<T> filterSupplier) {
    this.mongoUrl = mongoUrl;
    this.queryGenerator = queryGenerator;
    this.database = database;
    this.collectionSupplier = collectionSupplier;
    this.filterSupplier = filterSupplier;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final ConnectionString connString = new ConnectionString(mongoUrl);
    final MongoClientSettings settings =
        MongoClientSettings.builder().applyConnectionString(connString).retryWrites(true).build();
    mongo = MongoClients.create(settings);
  }

  @Override
  public void close() throws Exception {
    mongo.close();
  }

  @Override
  public void invoke(final T input, final Context context) throws Exception {
    final var collectionName = collectionSupplier.supply(input);
    final var conn = mongo.getDatabase(database);

    final var query = queryGenerator.query(input);
    final var collection = conn.getCollection(collectionName);

    System.out.println("QUERY " + collectionName + " " + query.document());
    if (query.action().equals(Query.Action.insert)) {
      collection.insertOne(query.document());

    } else if (query.action().equals(Query.Action.update)) {
      final var options = new UpdateOptions();
      final var filter = filterSupplier.supply(input);
      options.upsert(true);

      collection.updateOne(filter, query.document(), options);
    }
  }
}
