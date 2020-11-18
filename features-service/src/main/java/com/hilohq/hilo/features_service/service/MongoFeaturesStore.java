package com.hilohq.hilo.features_service.service;

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

public final class MongoFeaturesStore implements SchemaStore {
  private final MongoClient client;

  public MongoFeaturesStore(final MongoClient client) {
    this.client = client;
  }

  public static MongoFeaturesStore open(final String url) {
    final var codecRegistry =
        CodecRegistries.fromCodecs(
            new MongoProtobufCodec<>(FeatureOuterClass.FeaturesModel.newBuilder().build()),
            new MongoProtobufCodec<>(FeatureOuterClass.FeatureLogsModel.newBuilder().build()));

    final var settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(url))
            .codecRegistry(codecRegistry)
            .build();
    return new MongoFeaturesStore(MongoClients.create(settings));
  }

  @Override
  public CompletableFuture<FeatureOuterClass.FeaturesModel> getFeatures(String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.findOne("test", "features", filter, FeatureOuterClass.FeaturesModel.class);
  }

  @Override
  public CompletableFuture<Collection<FeatureOuterClass.FeatureLogsModel>> getFeatureLogs(
      String schemaId) {
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(schemaId));
    return client.find("test", "feature_logs", filter, FeatureOuterClass.FeatureLogsModel.class);
  }

  public void close() {
    client.close();
  }
}
