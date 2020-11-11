package com.hilohq.hilo.parsers.value;

public final class BytesValue implements Value {
  private final byte[] value;

  public BytesValue(final byte[] value) {
    this.value = value;
  }

  @Override
  public boolean isBytes() {
    return true;
  }

  @Override
  public byte[] bytesValue() {
    return value;
  }

  @Override
  public Object value() {
    return bytesValue();
  }

  @Override
  public ValueType type() {
    return ValueType.Bytes;
  }
}
