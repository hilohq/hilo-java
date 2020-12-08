package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class GrafanaDashboardAnnotations {
  private final List<GrafanaDashboardAnnotation> list;

  public static class Builder {
    private final List<GrafanaDashboardAnnotation> list = new ArrayList<>();

    public Builder list(final List<GrafanaDashboardAnnotation> list) {
      this.list.addAll(list);
      return this;
    }

    public GrafanaDashboardAnnotations build() {
      return new GrafanaDashboardAnnotations(list);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public GrafanaDashboardAnnotations(
      @JsonProperty("list") final List<GrafanaDashboardAnnotation> list) {
    this.list = list;
  }

  @JsonProperty("list")
  public List<GrafanaDashboardAnnotation> list() {
    return list;
  }
}
