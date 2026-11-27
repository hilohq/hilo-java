package com.hilohq.hilo.integrations_service.service;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import com.hilohq.hilo.connectors_mongo.client.MongoClients;
import com.hilohq.hilo.connectors_mongo.codec.MongoProtobufCodec;
import com.hilohq.hilo.protobufs.integrations.jdbc.Jdbc;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import java.util.concurrent.CompletableFuture;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistries;

public final class MongoIntegrationsStore implements IntegrationsStore {
  private final MongoClient client;
  private final String database;

  public MongoIntegrationsStore(final MongoClient client) {
    this(client, HiloConfig.fromEnvironment().mongoDatabase());
  }

  public MongoIntegrationsStore(final MongoClient client, final String database) {
    this.client = client;
    this.database = database;
  }

  /** Opens the store on the database configured by {@code HILO_MONGO_DATABASE}. */
  public static MongoIntegrationsStore open(final String url) {
    return open(url, HiloConfig.fromEnvironment().mongoDatabase());
  }

  public static MongoIntegrationsStore open(final String url, final String database) {
    final var codecRegistry =
        CodecRegistries.fromCodecs(
            new MongoProtobufCodec<>(Prometheus.PrometheusMetricsModel.newBuilder().build()),
            new MongoProtobufCodec<>(Jdbc.JdbcMetricsModel.newBuilder().build()));

    final var settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(url))
            .codecRegistry(codecRegistry)
            .build();
    return new MongoIntegrationsStore(MongoClients.create(settings), database);
  }

  @Override
  public CompletableFuture<Prometheus.PrometheusMetricsModel> getPrometheusMetrics(
      String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.findOne(database, "prometheus", filter, Prometheus.PrometheusMetricsModel.class);
  }

  @Override
  public CompletableFuture<Jdbc.JdbcMetricsModel> getJdbcMetrics(String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.findOne(database, "jdbc", filter, Jdbc.JdbcMetricsModel.class);
  }

  public void close() {
    client.close();
  }
}
