package com.hilohq.hilo.structure_learning.schema.json;

import static com.hilohq.hilo.structure_learning.serializer.ProtobufMessageToDocumentSerializer.serializedMessage;

import com.hilohq.hilo.protobufs.structure.schema.Json;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import org.bson.Document;

public final class JsonMongoUtils {
  private JsonMongoUtils() {}

  public static Document jsonFieldToUpdateDocument(
      final Schema.SchemaMetadata metadata, final Json.JsonField field) {
    final Document query = new Document();
    final Document setOp = new Document();

    setOp.put("json.fields." + field.getKey(), Document.parse(serializedMessage(field)));
    setOp.put("metadata", Document.parse(serializedMessage(metadata)));
    query.put("$set", setOp);

    return query;
  }
}
