package com.hilohq.hilo.integration_prometheus.stages;

import com.hilohq.hilo.connectors_mongo.output.MongoCommand;
import com.hilohq.hilo.connectors_mongo.output.MongoCommandGenerator;
import com.hilohq.hilo.connectors_mongo.output.MongoUpdate;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.structure_learning.serializer.ProtobufMessageToDocumentSerializer;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

public final class PrometheusMetricUpdateMongoCommandGenerator
    implements MongoCommandGenerator<Prometheus.PrometheusEvent> {

  private static Document metricToUpdateDocument(
      final Prometheus.PrometheusMetadata metadata, final Prometheus.PrometheusMetric metric) {
    final Document query = new Document();
    final Document setOp = new Document();

    setOp.put(
        "metrics." + metric.getKey(),
        Document.parse(ProtobufMessageToDocumentSerializer.serializedMessage(metric)));
    setOp.put(
        "metadata",
        Document.parse(ProtobufMessageToDocumentSerializer.serializedMessage(metadata)));
    query.put("$set", setOp);

    return query;
  }

  private MongoCommand generateMetric(
      final String database,
      final Prometheus.PrometheusMetadata metadata,
      final Prometheus.PrometheusMetric metric) {
    final var document = metricToUpdateDocument(metadata, metric);
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(metadata.getSchemaId()));
    return new MongoUpdate(database, "prometheus", filter, document);
  }

  @Override
  public MongoCommand generate(final String database, final Prometheus.PrometheusEvent value) {
    if (value.hasMetric()) {
      return generateMetric(database, value.getMetadata(), value.getMetric());
    } else {
      throw new IllegalArgumentException("unknown prometheus update type");
    }
  }
}
