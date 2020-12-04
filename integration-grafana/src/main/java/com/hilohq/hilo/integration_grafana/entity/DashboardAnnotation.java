package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Verify.verifyNotNull;

public final class DashboardAnnotation {
  private final long builtIn;
  private final String datasource;
  private final boolean enable;
  private final boolean hide;
  private final String iconColor;
  private final String name;
  private final String type;

  public static final class Builder {
    private long builtIn;
    private String datasource;
    private boolean enable;
    private boolean hide;
    private String iconColor;
    private String name;
    private String type;

    public Builder builtIn(final long builtIn) {
      this.builtIn = builtIn;
      return this;
    }

    public Builder datasource(final String datasource) {
      verifyNotNull(datasource);
      this.datasource = datasource;
      return this;
    }

    public Builder enable(final boolean enable) {
      this.enable = enable;
      return this;
    }

    public Builder hide(final boolean hide) {
      this.hide = hide;
      return this;
    }

    public Builder iconColor(final String iconColor) {
      verifyNotNull(iconColor);
      this.iconColor = iconColor;
      return this;
    }

    public Builder name(final String name) {
      verifyNotNull(name);
      this.name = name;
      return this;
    }

    public Builder type(final String type) {
      verifyNotNull(type);
      this.type = type;
      return this;
    }

    public DashboardAnnotation build() {
      return new DashboardAnnotation(builtIn, datasource, enable, hide, iconColor, name, type);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public DashboardAnnotation(
      @JsonProperty("builtIn") final long builtIn,
      @JsonProperty("datasource") final String datasource,
      @JsonProperty("enable") final boolean enable,
      @JsonProperty("hide") final boolean hide,
      @JsonProperty("iconColor") final String iconColor,
      @JsonProperty("name") final String name,
      @JsonProperty("type") final String type) {
    this.builtIn = builtIn;
    this.datasource = datasource;
    this.enable = enable;
    this.hide = hide;
    this.iconColor = iconColor;
    this.name = name;
    this.type = type;
  }

  @JsonProperty("builtIn")
  public long builtIn() {
    return builtIn;
  }

  @JsonProperty("datasource")
  public String datasource() {
    return datasource;
  }

  @JsonProperty("enable")
  public boolean enable() {
    return enable;
  }

  @JsonProperty("hide")
  public boolean hide() {
    return hide;
  }

  @JsonProperty("iconColor")
  public String iconColor() {
    return iconColor;
  }

  @JsonProperty("name")
  public String name() {
    return name;
  }

  @JsonProperty("type")
  public String type() {
    return type;
  }
}
