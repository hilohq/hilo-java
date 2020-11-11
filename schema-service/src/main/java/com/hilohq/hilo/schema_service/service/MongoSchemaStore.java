package com.hilohq.hilo.schema_service.service;

import com.hilohq.hilo.connectors.mongodb.client.MongoClient;
import com.hilohq.hilo.connectors.mongodb.client.MongoClients;
import com.hilohq.hilo.connectors.mongodb.codec.MongoProtobufCodec;
import com.hilohq.hilo.protobufs.structure.Schema;
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
    return new MongoSchemaStore(MongoClients.create(settings));
  }

  @Override
  public CompletableFuture<Collection<Schema.SchemaMetadataModel>> getSchemaIdsBySource(
      final String sourceId) {
    final var filter = new BsonDocument();
    filter.append("metadata.sourceId", new BsonString(sourceId));
    return client.find("test", "schemas", filter, Schema.SchemaMetadataModel.class);
  }

  @Override
  public CompletableFuture<Schema.SchemaFieldsModel> getSchema(final String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.findOne("test", "schemas", filter, Schema.SchemaFieldsModel.class);
  }

  @Override
  public CompletableFuture<Collection<Schema.SchemaLogsModel>> getSchemaLogs(final String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.find("test", "schema_logs", filter, Schema.SchemaLogsModel.class);
  }
}
