package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.hilohq.hilo.connector.flink.mongodb.*;
import com.hilohq.hilo.protobufs.schema.Schema;
import org.bson.BsonDocument;
import org.bson.BsonString;

public final class JsonSchemaUpdateCommandGenerator
    implements MongoCommandGenerator<Schema.SchemaUpdate> {
  @Override
  public MongoCommand<Schema.SchemaUpdate> generate(final Schema.SchemaUpdate value) {
    if (!value.hasJson()) {
      throw new IllegalArgumentException("JsonSchemaUpdateCommandGenerator only supports JSON");
    }

    if (value.getJson().hasSchemaUpdate()) {
      final var filter = new BsonDocument();
      filter.append("metadata.schemaId", new BsonString(value.getMetadata().getSchemaId()));

      return new MongoUpdate<>("schemas", filter, new JsonSchemaUpdateToDocumentSerializer());

    } else if (value.getJson().hasFieldLogs()) {
      return new MongoInsert<>("schema_logs", new ProtobufMessageToDocumentSerializer<>());

    } else if (value.getJson().hasParseErrors()) {
      return new MongoInsert<>("schema_errors", new ProtobufMessageToDocumentSerializer<>());

    } else {
      return new MongoNoop<>();
    }
  }
}
