package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PanelLegend {
  private final boolean avg;
  private final boolean current;
  private final boolean max;
  private final boolean min;
  private final boolean show;
  private final boolean total;
  private final boolean values;

  public static final class Builder {
    private boolean avg;
    private boolean current;
    private boolean max;
    private boolean min;
    private boolean show;
    private boolean total;
    private boolean values;

    public Builder avg(boolean avg) {
      this.avg = avg;
      return this;
    }

    public Builder current(boolean current) {
      this.current = current;
      return this;
    }

    public Builder max(boolean max) {
      this.max = max;
      return this;
    }

    public Builder min(boolean min) {
      this.min = min;
      return this;
    }

    public Builder show(boolean show) {
      this.show = show;
      return this;
    }

    public Builder total(boolean total) {
      this.total = total;
      return this;
    }

    public Builder values(boolean values) {
      this.values = values;
      return this;
    }

    public PanelLegend build() {
      return new PanelLegend(avg, current, max, min, show, total, values);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public PanelLegend(
      @JsonProperty("avg") final boolean avg,
      @JsonProperty("current") final boolean current,
      @JsonProperty("max") final boolean max,
      @JsonProperty("min") final boolean min,
      @JsonProperty("show") final boolean show,
      @JsonProperty("total") final boolean total,
      @JsonProperty("values") final boolean values) {
    this.avg = avg;
    this.current = current;
    this.max = max;
    this.min = min;
    this.show = show;
    this.total = total;
    this.values = values;
  }

  @JsonProperty("avg")
  public boolean avg() {
    return avg;
  }

  @JsonProperty("current")
  public boolean current() {
    return current;
  }

  @JsonProperty("max")
  public boolean max() {
    return max;
  }

  @JsonProperty("min")
  public boolean min() {
    return min;
  }

  @JsonProperty("show")
  public boolean show() {
    return show;
  }

  @JsonProperty("total")
  public boolean total() {
    return total;
  }

  @JsonProperty("values")
  public boolean values() {
    return values;
  }
}
