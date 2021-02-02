package com.hilohq.hilo.integration_jdbc.stages;

import com.hilohq.hilo.connectors_mongo.output.MongoCommand;
import com.hilohq.hilo.connectors_mongo.output.MongoCommandGenerator;
import com.hilohq.hilo.connectors_mongo.output.MongoUpdate;
import com.hilohq.hilo.protobufs.integrations.jdbc.Jdbc;
import com.hilohq.hilo.protobufs.integrations.jdbc.Mysql;
import com.hilohq.hilo.structure_learning.serializer.ProtobufMessageToDocumentSerializer;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

public final class JdbcMysqlTableUpdateMongoCommandGenerator
    implements MongoCommandGenerator<Jdbc.JdbcEvent> {

  private static Document metricToUpdateDocument(
      final Jdbc.JdbcMetadata metadata, final Mysql.MysqlMetric metric) {
    final Document query = new Document();
    final Document setOp = new Document();

    setOp.put(
        "mysql.metrics." + metric.getName(),
        Document.parse(ProtobufMessageToDocumentSerializer.serializedMessage(metric)));
    setOp.put(
        "metadata",
        Document.parse(ProtobufMessageToDocumentSerializer.serializedMessage(metadata)));
    query.put("$set", setOp);

    return query;
  }

  private MongoCommand generateMysql(
      final String database, final Jdbc.JdbcMetadata metadata, final Mysql.MysqlEvent event) {
    final var document = metricToUpdateDocument(metadata, event.getMetric());
    final var filter = new BsonDocument();
    filter.append("metadata.schemaId", new BsonString(metadata.getSchemaId()));
    return new MongoUpdate(database, "jdbc", filter, document);
  }

  @Override
  public MongoCommand generate(final String database, final Jdbc.JdbcEvent value) {
    if (value.hasMysql()) {
      return generateMysql(database, value.getMetadata(), value.getMysql());
    } else {
      throw new IllegalArgumentException("unknown prometheus update type");
    }
  }
}
