package com.hilohq.hilo.parsers.value;

public class TimestampValue implements Value {
  private final long value;

  public TimestampValue(final long value) {
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
    return value;
  }

  @Override
  public ValueType type() {
    return ValueType.Timestamp;
  }
}
