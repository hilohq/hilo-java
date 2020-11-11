package com.hilohq.hilo.structure_learning.serializer;

import com.google.protobuf.Message;
import org.apache.flink.api.common.serialization.SerializationSchema;

public final class ProtobufSerializationSchema<T extends Message>
    implements SerializationSchema<T> {
  @Override
  public byte[] serialize(T element) {
    return element.toByteArray();
  }
}
