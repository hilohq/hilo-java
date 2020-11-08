package com.hilohq.hilo.structure_learning.parsing.value;

public final class FloatValue implements Value {
  private final float value;

  public FloatValue(final float value) {
    this.value = value;
  }

  public float value() {
    return value;
  }
}
