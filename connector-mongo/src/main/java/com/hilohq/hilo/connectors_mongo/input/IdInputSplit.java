package com.hilohq.hilo.connectors_mongo.input;

import org.apache.flink.core.io.InputSplit;

public final class IdInputSplit implements InputSplit {
  private final int splitNumber;

  public IdInputSplit(final int splitNumber) {
    this.splitNumber = splitNumber;
  }

  @Override
  public int getSplitNumber() {
    return splitNumber;
  }
}
