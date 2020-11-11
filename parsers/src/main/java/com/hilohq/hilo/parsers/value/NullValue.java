package com.hilohq.hilo.parsers.value;

public final class NullValue implements Value {
  @Override
  public Object value() {
    return null;
  }

  @Override
  public ValueType type() {
    return ValueType.Null;
  }
}
