package com.hilohq.hilo.parsers.value;

public interface Value {
  default boolean isBoolean() {
    return false;
  }

  default boolean isInt() {
    return false;
  }

  default boolean isLong() {
    return false;
  }

  default boolean isFloat() {
    return false;
  }

  default boolean isDouble() {
    return false;
  }

  default boolean isString() {
    return false;
  }

  default boolean isBytes() {
    return false;
  }

  default boolean booleanValue() {
    throw new UnsupportedOperationException("not a boolean");
  }

  default int intValue() {
    throw new UnsupportedOperationException("not an int");
  }

  default long longValue() {
    throw new UnsupportedOperationException("not a long");
  }

  default float floatValue() {
    throw new UnsupportedOperationException("not a float");
  }

  default double doubleValue() {
    throw new UnsupportedOperationException("not a double");
  }

  default String stringValue() {
    throw new UnsupportedOperationException("not a String");
  }

  default byte[] bytesValue() {
    throw new UnsupportedOperationException("not bytes");
  }

  Object value();

  ValueType type();
}
