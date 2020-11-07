package com.hilohq.hilo.structure_learning.parsing.value;

public final class BooleanValue implements Value {
    private final boolean value;
    public BooleanValue(final boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }
}
