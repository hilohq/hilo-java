package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

public final class FileLineSource extends RichSourceFunction<TextLineBatch> {
  private final String filepath;
  private BufferedReader reader;

  public FileLineSource(final String filepath) {
    this.filepath = filepath;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    this.reader = new BufferedReader(new FileReader(filepath));
  }

  @Override
  public void run(final SourceContext<TextLineBatch> ctx) throws Exception {
    final var lines = reader.lines().collect(Collectors.toList());
    ctx.collect(new TextLineBatch(lines));
    ctx.close();
  }

  @Override
  public void cancel() {}
}
