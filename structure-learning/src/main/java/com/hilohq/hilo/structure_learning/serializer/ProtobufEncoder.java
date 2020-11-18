package com.hilohq.hilo.structure_learning.serializer;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.flink.api.common.serialization.Encoder;

public final class ProtobufEncoder<T extends Message> implements Encoder<T> {
  private final T message;
  private final JsonFormat.Printer printer;

  public ProtobufEncoder(T message) {
    this.message = message;
    this.printer = JsonFormat.printer();
  }

  @Override
  public void encode(final T element, final OutputStream stream) throws IOException {
    stream.write(printer.print(element).getBytes(StandardCharsets.UTF_8));
  }
}
