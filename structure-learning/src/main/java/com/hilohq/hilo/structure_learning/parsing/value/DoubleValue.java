package com.hilohq.hilo.structure_learning.parsing.value;

public final class DoubleValue implements Value {
  private final double value;

  public DoubleValue(final double value) {
    this.value = value;
  }

  @Override
  public double doubleValue() {
    return value;
  }

  public ValueType type() {
    return ValueType.Double;
  }
}
