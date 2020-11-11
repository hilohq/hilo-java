package com.hilohq.hilo.structure_learning.schema.base;

import com.hilohq.hilo.connectors.mongodb.flink.MongoCommand;
import com.hilohq.hilo.connectors.mongodb.flink.MongoCommandGenerator;
import com.hilohq.hilo.connectors.mongodb.flink.MongoInsert;
import com.hilohq.hilo.connectors.mongodb.flink.MongoUpdate;
import com.hilohq.hilo.protobufs.structure.Schema;
import com.hilohq.hilo.structure_learning.schema.json.JsonMongoUtils;
import com.hilohq.hilo.structure_learning.serializer.ProtobufMessageToDocumentSerializer;
import org.bson.BsonDocument;
import org.bson.BsonString;

public final class SchemaUpdateMongoCommandGenerator
    implements MongoCommandGenerator<Schema.SchemaUpdate> {
  private final ProtobufMessageToDocumentSerializer serializer;

  public SchemaUpdateMongoCommandGenerator() {
    this.serializer = new ProtobufMessageToDocumentSerializer();
  }

  private MongoCommand generateForJson(final String database, final Schema.SchemaUpdate update) {
    final var value = update.getJson();
    final var metadata = update.getMetadata();

    if (value.hasLog()) {
      final var model =
          Schema.SchemaLogsModel.newBuilder().setMetadata(metadata).setJson(value.getLog()).build();
      final var document = serializer.serialize(model);
      return new MongoInsert(database, "schema_logs", document);

    } else if (value.hasField()) {
      final var document = JsonMongoUtils.jsonFieldToUpdateDocument(metadata, value.getField());
      final var filter = new BsonDocument();
      filter.append("metadata.schemaId", new BsonString(metadata.getSchemaId()));
      return new MongoUpdate(database, "schemas", filter, document);

    } else {
      throw new IllegalArgumentException("Unexpected value received");
    }
  }

  @Override
  public MongoCommand generate(final String database, final Schema.SchemaUpdate value) {
    if (value.hasJson()) {
      return generateForJson(database, value);

    } else {
      throw new UnsupportedOperationException("unsupported for non json values");
    }
  }
}
