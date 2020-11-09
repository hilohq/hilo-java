package com.hilohq.hilo.structure_learning.parsing.value;

public interface Value {
  default int intValue() {
    throw new IllegalArgumentException(getClass() + " is not an int value");
  }

  default long longValue() {
    throw new IllegalArgumentException(getClass() + " is not a long value");
  }

  default float floatValue() {
    throw new IllegalArgumentException(getClass() + " is not a float value");
  }

  default double doubleValue() {
    throw new IllegalArgumentException(getClass() + " is not a double value");
  }

  default boolean boolValue() {
    throw new IllegalArgumentException(getClass() + " is not a bool value");
  }

  default String stringValue() {
    throw new IllegalArgumentException(getClass() + " is not a string value");
  }

  ValueType type();
}
