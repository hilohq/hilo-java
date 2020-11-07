package com.hilohq.hilo.structure_learning.parsing.value;

public final class LongValue implements Value {
  private final long value;

  public LongValue(final long value) {
    this.value = value;
  }

  public long value() {
    return value;
  }
}
