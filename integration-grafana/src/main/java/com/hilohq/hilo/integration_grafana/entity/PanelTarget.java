package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PanelTarget {
  private final String expr;
  private final String interval;
  private final String legendFormat;
  private final String refId;

  public static final class Builder {
    private String expr;
    private String interval;
    private String legendFormat;
    private String refId;

    public Builder expr(String expr) {
      this.expr = expr;
      return this;
    }

    public Builder interval(String interval) {
      this.interval = interval;
      return this;
    }

    public Builder legendFormat(String legendFormat) {
      this.legendFormat = legendFormat;
      return this;
    }

    public Builder refId(String refId) {
      this.refId = refId;
      return this;
    }

    public PanelTarget build() {
      return new PanelTarget(expr, interval, legendFormat, refId);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public PanelTarget(
      @JsonProperty("expr") final String expr,
      @JsonProperty("interval") final String interval,
      @JsonProperty("legendFormat") final String legendFormat,
      @JsonProperty("refId") final String refId) {
    this.expr = expr;
    this.interval = interval;
    this.legendFormat = legendFormat;
    this.refId = refId;
  }

  @JsonProperty("expr")
  public String expr() {
    return expr;
  }

  @JsonProperty("interval")
  public String interval() {
    return interval;
  }

  @JsonProperty("legendFormat")
  public String legendFormat() {
    return legendFormat;
  }

  @JsonProperty("refId")
  public String refId() {
    return refId;
  }
}
