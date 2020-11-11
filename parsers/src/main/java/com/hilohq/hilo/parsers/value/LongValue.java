package com.hilohq.hilo.parsers.value;

public final class LongValue implements Value {
  private final long value;

  public LongValue(final long value) {
    this.value = value;
  }

  @Override
  public boolean isLong() {
    return true;
  }

  @Override
  public long longValue() {
    return value;
  }

  @Override
  public Object value() {
    return longValue();
  }

  @Override
  public ValueType type() {
    return ValueType.Long;
  }
}
