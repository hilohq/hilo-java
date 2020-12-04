package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Verify.verifyNotNull;

public final class FieldConfig {
    public static final class Defaults {
        private final Custom custom;

        public static final class Custom { }

        @JsonCreator
        public Defaults(
                @JsonProperty("custom") final Custom custom
        ) {
            this.custom = custom;
        }

        @JsonProperty("custom")
        public Custom custom() {
            return custom;
        }
    }

    private final Defaults defaults;
    private final List<String> overrides;

    public static final class Builder {
        private Defaults defaults;
        private final List<String> overrides = new ArrayList<>();

        public Builder defaults(Defaults defaults) {
            verifyNotNull(defaults);
            this.defaults = defaults;
            return this;
        }

        public Builder overrides(List<String> overrides) {
            verifyNotNull(defaults);
            this.overrides.addAll(overrides);
            return this;
        }

        public FieldConfig build() {
            return new FieldConfig(defaults, overrides);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonCreator
    public FieldConfig(
            @JsonProperty("defaults") Defaults defaults,
            @JsonProperty("overrides") List<String> overrides
    ) {
        this.defaults = defaults;
        this.overrides = overrides;
    }

    @JsonProperty("defaults")
    public Defaults defaults() {
        return defaults;
    }

    @JsonProperty("overrides")
    public List<String> overrides() {
        return overrides;
    }
}
