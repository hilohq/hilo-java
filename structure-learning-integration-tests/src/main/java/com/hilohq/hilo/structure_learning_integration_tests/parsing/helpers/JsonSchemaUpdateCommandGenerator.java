package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.hilohq.hilo.connector.flink.mongodb.*;
import com.hilohq.hilo.protobufs.schema.Model;
import org.bson.BsonDocument;
import org.bson.BsonString;

public final class JsonSchemaUpdateCommandGenerator
    implements MongoCommandGenerator<Model.SchemaUpdate> {
  @Override
  public MongoCommand<Model.SchemaUpdate> generate(final Model.SchemaUpdate value) {
    if (!value.hasJson()) {
      throw new IllegalArgumentException("JsonSchemaUpdateCommandGenerator only supports JSON");
    }

    if (value.getJson().hasSchemaUpdate()) {
      final var filter = new BsonDocument();
      filter.append("metadata.schemaId", new BsonString(value.getMetadata().getSchemaId()));

      return new MongoUpdate<>("schemas", filter, new JsonSchemaUpdateToDocumentSerializer());

    } else if (value.getJson().hasFieldLogs()) {
      return new MongoInsert<>("schema_logs", new JsonSchemaLogsToDocumentSerializer());

    } else if (value.getJson().hasParseErrors()) {
      return new MongoInsert<>("schema_errors", new JsonSchemaErrorsToDocumentSerializer());

    } else {
      return new MongoNoop<>();
    }
  }
}
