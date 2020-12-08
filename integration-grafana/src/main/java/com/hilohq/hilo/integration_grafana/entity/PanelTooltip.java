package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PanelTooltip {
  private final boolean shared;
  private final int sort;
  private final String valueType;

  public static final class Builder {
    private boolean shared;
    private int sort;
    private String valueType;

    public Builder shared(final boolean shared) {
      this.shared = shared;
      return this;
    }

    public Builder sort(final int sort) {
      this.sort = sort;
      return this;
    }

    public Builder valueType(final String valueType) {
      this.valueType = valueType;
      return this;
    }

    public PanelTooltip build() {
      return new PanelTooltip(shared, sort, valueType);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public PanelTooltip(
      @JsonProperty("shared") final boolean shared,
      @JsonProperty("sort") final int sort,
      @JsonProperty("value_type") final String valueType) {
    this.shared = shared;
    this.sort = sort;
    this.valueType = valueType;
  }

  @JsonProperty("shared")
  public boolean shared() {
    return shared;
  }

  @JsonProperty("sort")
  public int sort() {
    return sort;
  }

  @JsonProperty("value_type")
  public String valueType() {
    return valueType;
  }
}
