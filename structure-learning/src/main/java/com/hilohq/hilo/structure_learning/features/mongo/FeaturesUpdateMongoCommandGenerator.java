package com.hilohq.hilo.structure_learning.features.mongo;

import com.hilohq.hilo.connectors.mongodb.flink.MongoCommand;
import com.hilohq.hilo.connectors.mongodb.flink.MongoCommandGenerator;
import com.hilohq.hilo.connectors.mongodb.flink.MongoInsert;
import com.hilohq.hilo.connectors.mongodb.flink.MongoUpdate;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;
import com.hilohq.hilo.structure_learning.serializer.ProtobufMessageToDocumentSerializer;
import org.bson.BsonDocument;
import org.bson.BsonString;

public final class FeaturesUpdateMongoCommandGenerator
    implements MongoCommandGenerator<FeatureOuterClass.FeaturesUpdate> {

  private final ProtobufMessageToDocumentSerializer serializer;

  public FeaturesUpdateMongoCommandGenerator() {
    this.serializer = new ProtobufMessageToDocumentSerializer();
  }

  private MongoCommand generateLog(
      final String database,
      final FeatureOuterClass.FeaturesMetadata metadata,
      final FeatureOuterClass.FeatureLog log) {
    final var model =
        FeatureOuterClass.FeatureLogsModel.newBuilder().setMetadata(metadata).setLogs(log).build();
    final var document = serializer.serialize(model);
    return new MongoInsert(database, "feature_logs", document);
  }

  private MongoCommand generateFeature(
      final String database,
      final FeatureOuterClass.FeaturesMetadata metadata,
      final FeatureOuterClass.Feature feature) {
    final var document = FeaturesMongoUtils.featureToUpdateDocument(metadata, feature);
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(metadata.getSchemaId()));
    return new MongoUpdate(database, "features", filter, document);
  }

  @Override
  public MongoCommand generate(
      final String database, final FeatureOuterClass.FeaturesUpdate value) {
    if (value.hasLog()) {
      return generateLog(database, value.getMetadata(), value.getLog());

    } else if (value.hasFeature()) {
      return generateFeature(database, value.getMetadata(), value.getFeature());

    } else {
      throw new UnsupportedOperationException("unsupported FeaturesUpdate");
    }
  }
}
