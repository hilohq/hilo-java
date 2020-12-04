package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class GridPos {
  private final int h;
  private final int w;
  private final int x;
  private final int y;

  public static final class Builder {
    private int h;
    private int w;
    private int x;
    private int y;

    public Builder h(final int h) {
      this.h = h;
      return this;
    }

    public Builder w(final int w) {
      this.w = w;
      return this;
    }

    public Builder x(final int x) {
      this.x = x;
      return this;
    }

    public Builder y(final int y) {
      this.y = y;
      return this;
    }

    public GridPos build() {
      return new GridPos(h, w, x, y);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public GridPos(
      @JsonProperty("h") final int h,
      @JsonProperty("w") final int w,
      @JsonProperty("x") final int x,
      @JsonProperty("y") final int y) {
    this.h = h;
    this.w = w;
    this.x = x;
    this.y = y;
  }

  @JsonProperty("h")
  public int h() {
    return h;
  }

  @JsonProperty("w")
  public int w() {
    return w;
  }

  @JsonProperty("x")
  public int x() {
    return x;
  }

  @JsonProperty("y")
  public int y() {
    return y;
  }
}
