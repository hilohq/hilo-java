package com.hilohq.hilo.schema_service.service;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import com.hilohq.hilo.connectors_mongo.client.MongoClients;
import com.hilohq.hilo.connectors_mongo.codec.MongoProtobufCodec;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistries;

public final class MongoSchemaStore implements SchemaStore {
  private final MongoClient client;
  private final String database;

  public MongoSchemaStore(final MongoClient client) {
    this(client, HiloConfig.fromEnvironment().mongoDatabase());
  }

  public MongoSchemaStore(final MongoClient client, final String database) {
    this.client = client;
    this.database = database;
  }

  /** Opens the store on the database configured by {@code HILO_MONGO_DATABASE}. */
  public static MongoSchemaStore open(final String url) {
    return open(url, HiloConfig.fromEnvironment().mongoDatabase());
  }

  public static MongoSchemaStore open(final String url, final String database) {
    final var codecRegistry =
        CodecRegistries.fromCodecs(
            new MongoProtobufCodec<>(Schema.SchemaMetadataModel.newBuilder().build()),
            new MongoProtobufCodec<>(Schema.SchemaFieldsModel.newBuilder().build()),
            new MongoProtobufCodec<>(Schema.SchemaLogsModel.newBuilder().build()));

    final var settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(url))
            .codecRegistry(codecRegistry)
            .build();
    return new MongoSchemaStore(MongoClients.create(settings), database);
  }

  @Override
  public CompletableFuture<Collection<Schema.SchemaMetadataModel>> getSchemaIdsBySource(
      final String sourceId) {
    final var filter = new BsonDocument();
    filter.append("metadata.sourceId", new BsonString(sourceId));
    return client.find(database, "schemas", filter, Schema.SchemaMetadataModel.class);
  }

  @Override
  public CompletableFuture<Schema.SchemaFieldsModel> getSchema(final String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.findOne(database, "schemas", filter, Schema.SchemaFieldsModel.class);
  }

  @Override
  public CompletableFuture<Collection<Schema.SchemaLogsModel>> getSchemaLogs(
      final String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.find(database, "schema_logs", filter, Schema.SchemaLogsModel.class);
  }

  public void close() {
    client.close();
  }
}
