package com.hilohq.hilo.parsers.value;

public final class BooleanValue implements Value {
  private final boolean value;

  public BooleanValue(final boolean value) {
    this.value = value;
  }

  @Override
  public boolean isBoolean() {
    return true;
  }

  @Override
  public boolean booleanValue() {
    return value;
  }

  @Override
  public Object value() {
    return booleanValue();
  }

  @Override
  public ValueType type() {
    return ValueType.Boolean;
  }
}
