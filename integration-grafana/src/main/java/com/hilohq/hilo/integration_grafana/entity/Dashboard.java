package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Verify.verifyNotNull;

public class Dashboard {
  private final DashboardAnnotations annotations;
  private final boolean editable;
  private final Long gnetId;
  private final long graphTooltip;
  private final List<DashboardPanel> panels;
  private final long schemaVersion;
  private final String style;
  private final DashboardTime time;
  private final DashboardTimePicker timePicker;
  private final String timezone;
  private final String title;

  public static class Builder {
    private DashboardAnnotations annotations =
        DashboardAnnotations.newBuilder().build();
    private boolean editable = false;
    private Long gnetId = null;
    private long graphTooltip = 0;
    private final List<DashboardPanel> panels = new ArrayList<>();
    private long schemaVersion = 0;
    private String style = "dark";
    private DashboardTime time = DashboardTime.newBuilder().build();
    private DashboardTimePicker timePicker = DashboardTimePicker.newBuilder().build();
    private String timezone = "";
    private String title = "";

    public Builder annotations(final DashboardAnnotations annotations) {
      verifyNotNull(annotations);
      this.annotations = annotations;
      return this;
    }

    public Builder editable(final boolean editable) {
      this.editable = editable;
      return this;
    }

    public Builder gnetId(final long gnetId) {
      this.gnetId = gnetId;
      return this;
    }

    public Builder graphTooltip(final long graphTooltip) {
      this.graphTooltip = graphTooltip;
      return this;
    }

    public Builder panels(final List<DashboardPanel> panels) {
      this.panels.addAll(panels);
      return this;
    }

    public Builder schemaVersion(final long schemaVersion) {
      this.schemaVersion = schemaVersion;
      return this;
    }

    public Builder style(final String style) {
      this.style = style;
      return this;
    }

    public Builder time(final DashboardTime time) {
      verifyNotNull(time);
      this.time = time;
      return this;
    }

    public Builder timePicker(final DashboardTimePicker timePicker) {
      verifyNotNull(timePicker);
      this.timePicker = timePicker;
      return this;
    }

    public Builder timezone(final String timezone) {
      this.timezone = timezone;
      return this;
    }

    public Builder title(final String title) {
      this.title = title;
      return this;
    }

    public Dashboard build() {
      return new Dashboard(
          annotations,
          editable,
          gnetId,
          graphTooltip,
          panels,
          schemaVersion,
          style,
          time,
          timePicker,
          timezone,
          title);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonCreator
  public Dashboard(
      @JsonProperty("annotations") final DashboardAnnotations annotations,
      @JsonProperty("editable") final boolean editable,
      @JsonProperty("gnetId") final Long gnetId,
      @JsonProperty("graphTooltip") final long graphTooltip,
      @JsonProperty("panels") final List<DashboardPanel> panels,
      @JsonProperty("schemaVersion") final long schemaVersion,
      @JsonProperty("style") final String style,
      @JsonProperty("time") final DashboardTime time,
      @JsonProperty("timepicker") final DashboardTimePicker timePicker,
      @JsonProperty("timezone") final String timezone,
      @JsonProperty("title") final String title) {
    this.annotations = annotations;
    this.editable = editable;
    this.gnetId = gnetId;
    this.graphTooltip = graphTooltip;
    this.panels = panels;
    this.schemaVersion = schemaVersion;
    this.style = style;
    this.time = time;
    this.timePicker = timePicker;
    this.timezone = timezone;
    this.title = title;
  }

  @JsonProperty("annotations")
  public DashboardAnnotations annotations() {
    return annotations;
  }

  @JsonProperty("editable")
  public boolean editable() {
    return editable;
  }

  @JsonProperty("gnetId")
  public Long gnetId() {
    return gnetId;
  }

  @JsonProperty("graphTooltip")
  public long graphTooltip() {
    return graphTooltip;
  }

  @JsonProperty("panels")
  public List<DashboardPanel> panels() {
    return panels;
  }

  @JsonProperty("schemaVersion")
  public long schemaVersion() {
    return schemaVersion;
  }

  @JsonProperty("style")
  public String style() {
    return style;
  }

  @JsonProperty("time")
  public DashboardTime time() {
    return time;
  }

  @JsonProperty("timepicker")
  public DashboardTimePicker timePicker() {
    return timePicker;
  }

  @JsonProperty("timezone")
  public String timezone() {
    return timezone;
  }

  @JsonProperty("title")
  public String title() {
    return title;
  }
}
