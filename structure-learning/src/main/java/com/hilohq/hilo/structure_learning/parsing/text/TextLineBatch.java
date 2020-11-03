package com.hilohq.hilo.structure_learning.parsing.text;

import com.hilohq.hilo.structure_learning.parsing.schema.Batch;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TextLineBatch implements Batch {

  private final List<String> lines;

  public TextLineBatch(final List<String> lines) {
    this.lines = lines;
  }

  public static TextLineBatch fromLines(final String... lines) {
    return new TextLineBatch(Arrays.asList(lines));
  }

  public List<String> lines() {
    return Collections.unmodifiableList(lines);
  }
}
