package com.hilohq.hilo.structure_learning.features.mongo;

import com.hilohq.hilo.connectors_mongo.sink.MongoCommand;
import com.hilohq.hilo.connectors_mongo.sink.MongoCommandGenerator;
import com.hilohq.hilo.connectors_mongo.sink.MongoInsert;
import com.hilohq.hilo.connectors_mongo.sink.MongoUpdate;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
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
    System.out.println("READ FEATURE " + feature);
    final var document = FeaturesMongoUtils.featureToUpdateDocument(metadata, feature);
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(metadata.getSchemaId()));
    System.out.println("WRITE FEATURE " + document.toJson());
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
