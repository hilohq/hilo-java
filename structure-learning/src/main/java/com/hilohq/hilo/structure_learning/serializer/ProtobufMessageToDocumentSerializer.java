package com.hilohq.hilo.structure_learning.serializer;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.connectors_mongo.sink.MongoIntoDocumentSerializer;
import org.bson.Document;

public final class ProtobufMessageToDocumentSerializer
    implements MongoIntoDocumentSerializer<Message> {
  public static String serializedMessage(final Message value) {
    try {
      return JsonFormat.printer().print(value);
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public Document serialize(final Message value) {
    final var serialized = serializedMessage(value);
    return Document.parse(serialized);
  }
}
