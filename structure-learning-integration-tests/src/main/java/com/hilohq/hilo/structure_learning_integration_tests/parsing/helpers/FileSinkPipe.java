package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public final class FileSinkPipe<T extends Message> {

  private final String filepath;

  public FileSinkPipe() {
    filepath = "/tmp/" + "test-" + UUID.randomUUID().toString() + ".txt";
  }

  private static class FileSink<T extends Message> implements SinkFunction<T> {

    private final String filepath;

    public FileSink(final String filepath) {
      this.filepath = filepath;
    }

    public void invoke(final T event, final Context context) throws Exception {
      final var os = new FileOutputStream(filepath, true);
      final var s = JsonFormat.printer().print(event).replace("\n", "") + "\n";
      final var bytes = s.getBytes(StandardCharsets.UTF_8);
      os.write(bytes);
      os.close();
    }
  }

  public SinkFunction<T> sink() {
    return new FileSink<>(filepath);
  }

  private T parse(final String line, final T message) {
    try {
      final var builder = message.newBuilderForType();
      JsonFormat.parser().merge(line, builder);
      return (T) builder.build();
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  public List<T> events(final T message) {
    try {
      final var reader = new BufferedReader(new FileReader(filepath));
      return reader.lines().map(line -> parse(line, message)).collect(Collectors.toList());
    } catch (FileNotFoundException e) {
      throw new UncheckedIOException(e);
    }
  }
}
