package com.hilohq.hilo.features_service.service;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.connectors_mongo.client.MongoClient;
import com.hilohq.hilo.connectors_mongo.client.MongoClients;
import com.hilohq.hilo.connectors_mongo.codec.MongoProtobufCodec;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistries;

public final class MongoFeaturesStore implements FeaturesStore {
  private final MongoClient client;
  private final String database;

  public MongoFeaturesStore(final MongoClient client) {
    this(client, HiloConfig.fromEnvironment().mongoDatabase());
  }

  public MongoFeaturesStore(final MongoClient client, final String database) {
    this.client = client;
    this.database = database;
  }

  /** Opens the store on the database configured by {@code HILO_MONGO_DATABASE}. */
  public static MongoFeaturesStore open(final String url) {
    return open(url, HiloConfig.fromEnvironment().mongoDatabase());
  }

  public static MongoFeaturesStore open(final String url, final String database) {
    final var codecRegistry =
        CodecRegistries.fromCodecs(
            new MongoProtobufCodec<>(FeatureOuterClass.FeaturesModel.newBuilder().build()),
            new MongoProtobufCodec<>(FeatureOuterClass.FeatureLogsModel.newBuilder().build()));

    final var settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(url))
            .codecRegistry(codecRegistry)
            .build();
    return new MongoFeaturesStore(MongoClients.create(settings), database);
  }

  @Override
  public CompletableFuture<FeatureOuterClass.FeaturesModel> getFeatures(String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.findOne(database, "features", filter, FeatureOuterClass.FeaturesModel.class);
  }

  @Override
  public CompletableFuture<Collection<FeatureOuterClass.FeatureLogsModel>> getFeatureLogs(
      String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.find(database, "feature_logs", filter, FeatureOuterClass.FeatureLogsModel.class);
  }

  public void close() {
    client.close();
  }
}
