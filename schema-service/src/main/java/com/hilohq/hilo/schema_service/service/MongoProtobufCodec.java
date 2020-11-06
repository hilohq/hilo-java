package com.hilohq.hilo.schema_service.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.*;

public final class MongoProtobufCodec<T extends Message> implements Codec<T> {
  private final T instance;
  private final DocumentCodec codec;

  public MongoProtobufCodec(final T instance) {
    this.instance = instance;
    this.codec = new DocumentCodec();
  }

  @Override
  public T decode(final BsonReader reader, final DecoderContext decoderContext) {
    final var document = codec.decode(reader, decoderContext);
    final var builder = instance.newBuilderForType();
    try {
      JsonFormat.parser().ignoringUnknownFields().merge(document.toJson(), builder);
      return (T) builder.build();
    } catch (InvalidProtocolBufferException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public void encode(final BsonWriter writer, final T value, final EncoderContext encoderContext) {
    try {
      final var document = Document.parse(JsonFormat.printer().print(value));
      codec.encode(writer, document, encoderContext);
    } catch (InvalidProtocolBufferException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public Class<T> getEncoderClass() {
    return (Class<T>) instance.getClass();
  }
}
