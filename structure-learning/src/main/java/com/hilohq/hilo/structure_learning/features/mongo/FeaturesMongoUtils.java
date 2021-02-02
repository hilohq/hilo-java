package com.hilohq.hilo.structure_learning.features.mongo;

import static com.hilohq.hilo.structure_learning.serializer.ProtobufMessageToDocumentSerializer.serializedMessage;

import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import org.bson.Document;

public final class FeaturesMongoUtils {
  private FeaturesMongoUtils() {}

  public static Document featureToUpdateDocument(
      final FeatureOuterClass.FeaturesMetadata metadata, final FeatureOuterClass.Feature feature) {
    final Document query = new Document();
    final Document setOp = new Document();
    final Document featureDocument = new Document();

    featureDocument.put("stats", Document.parse(serializedMessage(feature.getStats())));
    featureDocument.put(
        "featureClass", Document.parse(serializedMessage(feature.getFeatureClass())));
    featureDocument.put("key", feature.getKey());
    setOp.put("features." + feature.getKey(), featureDocument);
    setOp.put("metadata", Document.parse(serializedMessage(metadata)));
    query.put("$set", setOp);

    return query;
  }
}
