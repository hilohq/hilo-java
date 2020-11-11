package com.hilohq.hilo.structure_learning.serializer;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.connectors.mongodb.flink.MongoIntoDocumentSerializer;
import java.nio.charset.StandardCharsets;
import org.bson.Document;

public final class ProtobufMessageToDocumentSerializer
    implements MongoIntoDocumentSerializer<Message> {
  public static String serializedMessage(final Message value) {
    try {
      final var bytes = JsonFormat.printer().print(value).getBytes(StandardCharsets.UTF_8);
      return new String(bytes, StandardCharsets.UTF_8);
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
