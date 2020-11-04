package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import static com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.ProtobufMessageToDocumentSerializer.serializedMessage;

import com.hilohq.hilo.connector.flink.mongodb.MongoIntoDocumentSerializer;
import com.hilohq.hilo.protobufs.schema.Schema;
import org.bson.Document;

public final class JsonSchemaUpdateToDocumentSerializer
    implements MongoIntoDocumentSerializer<Schema.SchemaUpdate> {

  @Override
  public Document serialize(final Schema.SchemaUpdate value) {
    final var update = value.getJson().getSchemaUpdate();
    final Document query = new Document();
    final Document setOp = new Document();

    if (update.hasStats()) {
      setOp.put("stats", Document.parse(serializedMessage(update.getStats())));
    }

    for (final var field : update.getFieldsList()) {
      setOp.put(
          "fields.json." + field.getField().getName(), Document.parse(serializedMessage(field)));
    }

    if (!setOp.isEmpty()) {
      setOp.put("metadata", Document.parse(serializedMessage(value.getMetadata())));
      query.put("$set", setOp);
    }

    return query;
  }
}
