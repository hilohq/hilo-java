package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.connector.flink.mongodb.MongoIntoDocumentSerializer;
import java.nio.charset.StandardCharsets;
import org.bson.Document;

public final class ProtobufMessageToDocumentSerializer<T extends Message>
    implements MongoIntoDocumentSerializer<T> {
  public static <T extends Message> String serializedMessage(final T value) {
    try {
      final var bytes = JsonFormat.printer().print(value).getBytes(StandardCharsets.UTF_8);
      return new String(bytes, StandardCharsets.UTF_8);
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public Document serialize(final T value) {
    final var serialized = serializedMessage(value);
    return Document.parse(serialized);
  }
}
