package com.hilohq.hilo.parsers.value;

public final class DoubleValue implements Value {
    private final double value;

    public DoubleValue(final double value) {
        this.value = value;
    }

    @Override
    public boolean isDouble() { return true; }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public ValueType type() {
        return ValueType.Double;
    }
}
