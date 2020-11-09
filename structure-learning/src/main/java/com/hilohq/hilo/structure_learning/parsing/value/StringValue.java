package com.hilohq.hilo.structure_learning.parsing.value;

public final class StringValue implements Value {
  private final String value;

  public StringValue(final String value) {
    this.value = value;
  }

  @Override
  public String stringValue() {
    return value;
  }

  public ValueType type() {
    return ValueType.String;
  }
}
