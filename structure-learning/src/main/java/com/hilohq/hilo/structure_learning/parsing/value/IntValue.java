package com.hilohq.hilo.structure_learning.parsing.value;

public final class IntValue implements Value {
  private final int value;

  public IntValue(final int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }
}
