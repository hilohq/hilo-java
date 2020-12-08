package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class XAxis {
  private final String mode;
  private final String name;
  private final boolean show;
  private final List<String> values;

  public static final class Builder {
    private String mode;
    private String name;
    private boolean show;
    private final List<String> values = new ArrayList<>();

    public Builder mode(final String mode) {
      this.mode = mode;
      return this;
    }

    public Builder name(final String name) {
      this.name = name;
      return this;
    }

    public Builder show(final boolean show) {
      this.show = show;
      return this;
    }

    public Builder values(final List<String> values) {
      this.values.addAll(values);
      return this;
    }

    public XAxis build() {
      return new XAxis(mode, name, show, values);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public XAxis(
      @JsonProperty("mode") final String mode,
      @JsonProperty("name") final String name,
      @JsonProperty("show") final boolean show,
      @JsonProperty("values") final List<String> values) {
    this.mode = mode;
    this.name = name;
    this.show = show;
    this.values = values;
  }

  @JsonProperty("mode")
  public String mode() {
    return mode;
  }

  @JsonProperty("name")
  public String name() {
    return name;
  }

  @JsonProperty("show")
  public boolean show() {
    return show;
  }

  @JsonProperty("values")
  public List<String> values() {
    return values;
  }
}
