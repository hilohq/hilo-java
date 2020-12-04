package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Verify.verifyNotNull;

public final class DashboardTime {
    private final String from;
    private final String to;

    public static final class Builder {
        private String from = "now-30m";
        private String to = "now";

        public Builder from(final String from) {
            verifyNotNull(from);
            this.from = from;
            return this;
        }

        public Builder to(final String to) {
            verifyNotNull(to);
            this.to = to;
            return this;
        }

        public DashboardTime build() {
            verifyNotNull(from);
            verifyNotNull(to);
            return new DashboardTime(from, to);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonCreator
    public DashboardTime(
            @JsonProperty("from") final String from,
            @JsonProperty("to") final String to
    ) {
        this.from = from;
        this.to = to;
    }

    @JsonProperty("from")
    public String from() {
        return from;
    }

    @JsonProperty("to")
    public String to() {
        return to;
    }
}
