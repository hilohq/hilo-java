package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.google.protobuf.Message;
import java.io.IOException;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

public final class ProtobufDeserializationSchema<T extends Message>
    implements DeserializationSchema<T> {

  private final T instance;

  public ProtobufDeserializationSchema(final T instance) {
    this.instance = instance;
  }

  @Override
  public T deserialize(byte[] message) throws IOException {
    return (T) instance.newBuilderForType().mergeFrom(message).build();
  }

  @Override
  public boolean isEndOfStream(T nextElement) {
    return false;
  }

  @Override
  public TypeInformation<T> getProducedType() {
    return (TypeInformation<T>) TypeInformation.of(instance.getClass());
  }
}
