package com.hilohq.hilo.integration_grafana.entity;

import static com.google.common.base.Verify.verifyNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class GrafanaDashboard {
  private final GrafanaDashboardAnnotations annotations;
  private final boolean editable;
  private final Long gnetId;
  private final long graphTooltip;
  private final List<GrafanaDashboardPanel> panels;
  private final long schemaVersion;
  private final String style;
  private final GrafanaDashboardTime time;
  private final GrafanaDashboardTimePicker timePicker;
  private final String timezone;
  private final String title;

  public static class Builder {
    private GrafanaDashboardAnnotations annotations =
        GrafanaDashboardAnnotations.newBuilder().build();
    private boolean editable = false;
    private Long gnetId = null;
    private long graphTooltip = 0;
    private final List<GrafanaDashboardPanel> panels = new ArrayList<>();
    private long schemaVersion = 0;
    private String style = "dark";
    private GrafanaDashboardTime time = GrafanaDashboardTime.newBuilder().build();
    private GrafanaDashboardTimePicker timePicker = GrafanaDashboardTimePicker.newBuilder().build();
    private String timezone = "";
    private String title = "";

    public Builder annotations(final GrafanaDashboardAnnotations annotations) {
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

    public Builder panel(final GrafanaDashboardPanel panel) {
      this.panels.add(panel);
      return this;
    }

    public Builder panels(final List<GrafanaDashboardPanel> panels) {
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

    public Builder time(final GrafanaDashboardTime time) {
      verifyNotNull(time);
      this.time = time;
      return this;
    }

    public Builder timePicker(final GrafanaDashboardTimePicker timePicker) {
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

    public GrafanaDashboard build() {
      return new GrafanaDashboard(
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
  public GrafanaDashboard(
      @JsonProperty("annotations") final GrafanaDashboardAnnotations annotations,
      @JsonProperty("editable") final boolean editable,
      @JsonProperty("gnetId") final Long gnetId,
      @JsonProperty("graphTooltip") final long graphTooltip,
      @JsonProperty("panels") final List<GrafanaDashboardPanel> panels,
      @JsonProperty("schemaVersion") final long schemaVersion,
      @JsonProperty("style") final String style,
      @JsonProperty("time") final GrafanaDashboardTime time,
      @JsonProperty("timepicker") final GrafanaDashboardTimePicker timePicker,
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
  public GrafanaDashboardAnnotations annotations() {
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
  public List<GrafanaDashboardPanel> panels() {
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
  public GrafanaDashboardTime time() {
    return time;
  }

  @JsonProperty("timepicker")
  public GrafanaDashboardTimePicker timePicker() {
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
