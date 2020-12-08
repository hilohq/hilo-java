package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class YAxisFormat {
  private final String format;
  private final int logBase;
  private final boolean show;

  public static final class Builder {
    private String format;
    private int logBase;
    private boolean show;

    public Builder format(final String format) {
      this.format = format;
      return this;
    }

    public Builder logBase(final int logBase) {
      this.logBase = logBase;
      return this;
    }

    public Builder show(final boolean show) {
      this.show = show;
      return this;
    }

    public YAxisFormat build() {
      return new YAxisFormat(format, logBase, show);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public YAxisFormat(
      @JsonProperty("format") final String format,
      @JsonProperty("logBase") final int logBase,
      @JsonProperty("show") final boolean show) {
    this.format = format;
    this.logBase = logBase;
    this.show = show;
  }

  @JsonProperty("format")
  public final String format() {
    return format;
  }

  @JsonProperty("logBase")
  public final int logBase() {
    return logBase;
  }

  @JsonProperty("show")
  public final boolean show() {
    return show;
  }
}
