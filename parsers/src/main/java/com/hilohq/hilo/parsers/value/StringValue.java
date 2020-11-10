package com.hilohq.hilo.parsers.value;

public final class StringValue implements Value {
    private final String value;

    public StringValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean isString() { return true; }

    @Override
    public String stringValue() {
        return value;
    }

    @Override
    public ValueType type() {
        return ValueType.String;
    }
}
