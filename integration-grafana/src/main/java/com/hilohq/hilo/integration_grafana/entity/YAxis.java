package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class YAxis {
    private final boolean align;

    public static final class Builder {
        private boolean align;

        public Builder align(final boolean align) {
            this.align = align;
            return this;
        }

        public YAxis build() {
            return new YAxis(align);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonCreator
    public YAxis(
            @JsonProperty("align") final boolean align) {
        this.align = align;
    }

    @JsonProperty("align")
    public boolean align() {
        return align;
    }
}
