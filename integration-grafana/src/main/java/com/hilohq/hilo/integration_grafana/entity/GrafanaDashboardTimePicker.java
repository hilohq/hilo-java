package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class GrafanaDashboardTimePicker {

  private final List<String> refreshIntervals;

  public static final class Builder {
    private final List<String> refreshIntervals = new ArrayList<>();

    public Builder refreshIntervals(final List<String> refreshIntervals) {
      this.refreshIntervals.addAll(refreshIntervals);
      return this;
    }

    public GrafanaDashboardTimePicker build() {
      return new GrafanaDashboardTimePicker(refreshIntervals);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public GrafanaDashboardTimePicker(
      @JsonProperty("refresh_intervals") final List<String> refreshIntervals) {
    this.refreshIntervals = refreshIntervals;
  }

  @JsonProperty("refresh_intervals")
  public List<String> refreshIntervals() {
    return refreshIntervals;
  }
}
