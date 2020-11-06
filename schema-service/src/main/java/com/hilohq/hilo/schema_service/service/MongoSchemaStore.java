package com.hilohq.hilo.schema_service.service;

import com.hilohq.hilo.protobufs.schema.Model;
import com.hilohq.hilo.connector.mongodb.client.MongoClient;
import com.hilohq.hilo.connector.mongodb.client.MongoClients;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistries;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public final class MongoSchemaStore implements SchemaStore {
  private final MongoClient client;

  public MongoSchemaStore(final MongoClient client) {
    this.client = client;
  }

  public static MongoSchemaStore open(final String url) {
      final var codecRegistry = CodecRegistries.fromCodecs(
              new MongoProtobufCodec<>(Model.SchemaMetadataModel.newBuilder().build())
      );

      final var settings = MongoClientSettings.builder()
          .applyConnectionString(new ConnectionString(url))
          .codecRegistry(codecRegistry)
          .build();
    return new MongoSchemaStore(MongoClients.create(settings));
  }

  @Override
  public CompletableFuture<Collection<Model.SchemaMetadataModel>> getSchemaMetadata(
      final String sourceId) {
    final var filter = new BsonDocument();
    filter.append("metadata.sourceId", new BsonString(sourceId));

      return client.find("test", "schemas", filter, Model.SchemaMetadataModel.class);
  }

  @Override
  public CompletableFuture<Model.SchemaModel> getSchema(final String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));

    return client.findOne("test", "schemas", filter, Model.SchemaModel.class);
  }

  @Override
  public CompletableFuture<Collection<Model.SchemaFieldLog>> getSchemaLogs(final String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
      return client.find("test", "schema_logs", filter, Model.SchemaFieldLog.class);
  }

  @Override
  public CompletableFuture<Collection<Model.SchemaParseError>> getSchemaErrors(
      final String schemaId) {
      final var filter = new BsonDocument();
      filter.append("metadata.schemaId", new BsonString(schemaId));
      return client.find("test", "schema_errors", filter, Model.SchemaParseError.class);
  }
}
