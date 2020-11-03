package com.hilohq.hilo.hilo_flink.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.nio.charset.StandardCharsets;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bson.Document;

public final class MongoDbSink<T> extends RichSinkFunction<T> {

  private final String mongoUrl;
  private final SerializationSchema<T> schema;
  private final CollectionSupplier<T> collectionSupplier;
  private final String database;
  private transient MongoClient mongo;

  public MongoDbSink(
      final String mongoUrl,
      final SerializationSchema<T> schema,
      final String database,
      final String collection) {
    this(mongoUrl, schema, database, new SingleCollectionSupplier<>(collection));
  }

  public MongoDbSink(
      final String mongoUrl,
      final SerializationSchema<T> schema,
      final String database,
      final CollectionSupplier<T> collectionSupplier) {
    this.mongoUrl = mongoUrl;
    this.schema = schema;
    this.database = database;
    this.collectionSupplier = collectionSupplier;
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

    final var s = new String(schema.serialize(input), StandardCharsets.UTF_8);
    final var document = Document.parse(s);
    final var collection = conn.getCollection(collectionName);
    collection.insertOne(document);
  }
}
