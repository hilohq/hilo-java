package com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers;

import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import java.util.List;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public final class TextLineBatchSource implements SourceFunction<TextLineBatch> {
  private final List<TextLineBatch> batches;
  private int count;

  public TextLineBatchSource(final List<TextLineBatch> batches) {
    this.batches = batches;
    this.count = 0;
  }

  @Override
  public void run(final SourceContext<TextLineBatch> ctx) throws Exception {
    if (count == batches.size()) {
      ctx.close();
      return;
    }

    ctx.collect(batches.get(count));
    count++;
  }

  @Override
  public void cancel() {}
}
