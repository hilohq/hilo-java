package com.hilohq.hilo.connectors_mongo.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.*;
import org.bson.json.Converter;
import org.bson.json.JsonWriterSettings;
import org.bson.json.StrictJsonWriter;

public final class MongoProtobufCodec<T extends Message> implements Codec<T> {
  private final T instance;
  private final DocumentCodec codec;

  public MongoProtobufCodec(final T instance) {
    this.instance = instance;
    this.codec = new DocumentCodec();
  }

  private static class LongConverter implements Converter<Long> {
    @Override
    public void convert(Long value, StrictJsonWriter writer) {
      writer.writeString(value.toString());
    }
  }

  @Override
  public T decode(final BsonReader reader, final DecoderContext decoderContext) {
    final var document = codec.decode(reader, decoderContext);
    final var builder = instance.newBuilderForType();
    try {
      final var jsonString =
          document.toJson(JsonWriterSettings.builder().int64Converter(new LongConverter()).build());
      JsonFormat.parser().ignoringUnknownFields().merge(jsonString, builder);
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
