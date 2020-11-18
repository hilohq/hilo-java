package com.hilohq.hilo.parsers.value;

public final class FloatValue implements Value {
  private final float value;

  public FloatValue(final float value) {
    this.value = value;
  }

  @Override
  public boolean isFloat() {
    return true;
  }

  @Override
  public float floatValue() {
    return value;
  }

  @Override
  public Object value() {
    return floatValue();
  }

  @Override
  public double numericValue() {
    return value;
  }

  @Override
  public ValueType type() {
    return ValueType.Float;
  }
}
