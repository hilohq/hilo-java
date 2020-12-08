package com.hilohq.hilo.connectors_mongo.input;

import java.util.List;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.core.io.InputSplitAssigner;

public final class MongoInputSplitAssigner implements InputSplitAssigner {
  private final List<IdInputSplit> splits;

  public MongoInputSplitAssigner(final List<IdInputSplit> splits) {
    this.splits = splits;
  }

  @Override
  public InputSplit getNextInputSplit(String host, int taskId) {
    if (splits.isEmpty()) {
      return null;
    }

    return splits.remove(0);
  }

  @Override
  public void returnInputSplit(List<InputSplit> splits, int taskId) {
    for (final var split : splits) {
      if (!(split instanceof IdInputSplit)) {
        continue;
      }

      splits.add(split);
    }
  }
}
