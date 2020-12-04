package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public final class DashboardAnnotations {
    private final List<DashboardAnnotation> list;

    public static class Builder {
        private final List<DashboardAnnotation> list = new ArrayList<>();

        public Builder list(final List<DashboardAnnotation> list) {
            this.list.addAll(list);
            return this;
        }

        public DashboardAnnotations build() {
            return new DashboardAnnotations(list);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonCreator
    public DashboardAnnotations(
            @JsonProperty("list") final List<DashboardAnnotation> list
    ) {
        this.list = list;
    }

    @JsonProperty("list")
    public List<DashboardAnnotation> list() {
        return list;
    }
}
