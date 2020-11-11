package com.hilohq.hilo.parsers.value;

public final class IntValue implements Value {
  private final int value;

  public IntValue(final int value) {
    this.value = value;
  }

  @Override
  public boolean isInt() {
    return true;
  }

  @Override
  public int intValue() {
    return value;
  }

  @Override
  public Object value() {
    return intValue();
  }

  @Override
  public ValueType type() {
    return ValueType.Int;
  }
}
