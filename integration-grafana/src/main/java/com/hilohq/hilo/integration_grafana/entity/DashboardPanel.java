package com.hilohq.hilo.integration_grafana.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DashboardPanel {
  private final Map<String, String> aliasColors;
  private final boolean bars;
  private final int dashLength;
  private final boolean dashes;
  private final String datasource;
  private final FieldConfig fieldConfig;
  private final int fill;
  private final int fillGradient;
  private final GridPos gridPos;
  private final boolean hiddenSeries;
  private final int id;
  private final PanelLegend legend;
  private final boolean lines;
  private final int lineWidth;
  private final String nullPointMode;
  private final boolean percentage;
  private final int pointRadius;
  private final boolean points;
  private final String renderer;
  private final List<String> seriesOverrides;
  private final int spaceLength;
  private final boolean stack;
  private final boolean steppedLine;
  private final List<PanelTarget> targets;
  private final List<String> thresholds;
  private final String title;
  private final PanelTooltip tooltip;
  private final String type;
  private final XAxis xAxis;
  private final List<YAxisFormat> yAxes;
  private final YAxis yAxis;

  public static final class Builder {
    private Map<String, String> aliasColors = new HashMap<>();
    private boolean bars;
    private int dashLength;
    private boolean dashes;
    private String datasource;
    private FieldConfig fieldConfig;
    private int fill;
    private int fillGradient;
    private GridPos gridPos;
    private boolean hiddenSeries;
    private int id;
    private PanelLegend legend;
    private boolean lines;
    private int lineWidth;
    private String nullPointMode;
    private boolean percentage;
    private int pointRadius;
    private boolean points;
    private String renderer;
    private List<String> seriesOverrides = new ArrayList<>();
    private int spaceLength;
    private boolean stack;
    private boolean steppedLine;
    private List<PanelTarget> targets;
    private List<String> thresholds;
    private String title;
    private PanelTooltip tooltip;
    private String type;
    private XAxis xAxis;
    private List<YAxisFormat> yAxes;
    private YAxis yAxis;

    public Builder aliasColors(Map<String, String> aliasColors) {
      this.aliasColors.putAll(aliasColors);
      return this;
    }

    public Builder bars(boolean bars) {
      this.bars = bars;
      return this;
    }

    public Builder dashLength(int dashLength) {
      this.dashLength = dashLength;
      return this;
    }

    public Builder dashes(boolean dashes) {
      this.dashes = dashes;
      return this;
    }

    public Builder datasource(String datasource) {
      this.datasource = datasource;
      return this;
    }

    public Builder fieldConfig(FieldConfig fieldConfig) {
      this.fieldConfig = fieldConfig;
      return this;
    }

    public Builder fill(int fill) {
      this.fill = fill;
      return this;
    }

    public Builder fillGradient(int fillGradient) {
      this.fillGradient = fillGradient;
      return this;
    }

    public Builder gridPos(GridPos gridPos) {
      this.gridPos = gridPos;
      return this;
    }

    public Builder hiddenSeries(boolean hiddenSeries) {
      this.hiddenSeries = hiddenSeries;
      return this;
    }

    public Builder id(int id) {
      this.id = id;
      return this;
    }

    public Builder legend(PanelLegend legend) {
      this.legend = legend;
      return this;
    }

    public Builder lines(boolean lines) {
      this.lines = lines;
      return this;
    }

    public Builder lineWidth(int lineWidth) {
      this.lineWidth = lineWidth;
      return this;
    }

    public Builder nullPointMode(String nullPointMode) {
      this.nullPointMode = nullPointMode;
      return this;
    }

    public Builder percentage(boolean percentage) {
      this.percentage = percentage;
      return this;
    }

    public Builder pointRadius(int pointRadius) {
      this.pointRadius = pointRadius;
      return this;
    }

    public Builder points(boolean points) {
      this.points = points;
      return this;
    }

    public Builder renderer(String renderer) {
      this.renderer = renderer;
      return this;
    }

    public Builder seriesOverrides(List<String> seriesOverrides) {
      this.seriesOverrides.addAll(seriesOverrides);
      return this;
    }

    public Builder spaceLength(int spaceLength) {
      this.spaceLength = spaceLength;
      return this;
    }

    public Builder stack(boolean stack) {
      this.stack = stack;
      return this;
    }

    public Builder steppedLine(boolean steppedLine) {
      this.steppedLine = steppedLine;
      return this;
    }

    public Builder targets(List<PanelTarget> targets) {
      this.targets = targets;
      return this;
    }

    public Builder thresholds(List<String> thresholds) {
      this.thresholds = thresholds;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder tooltip(PanelTooltip tooltip) {
      this.tooltip = tooltip;
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder xAxis(XAxis xAxis) {
      this.xAxis = xAxis;
      return this;
    }

    public Builder yAxes(List<YAxisFormat> yAxes) {
      this.yAxes = yAxes;
      return this;
    }

    public Builder yAxis(YAxis yAxis) {
      this.yAxis = yAxis;
      return this;
    }

    public DashboardPanel build() {
      return new DashboardPanel(
          aliasColors,
          bars,
          dashLength,
          dashes,
          datasource,
          fieldConfig,
          fill,
          fillGradient,
          gridPos,
          hiddenSeries,
          id,
          legend,
          lines,
          lineWidth,
          nullPointMode,
          percentage,
          pointRadius,
          points,
          renderer,
          seriesOverrides,
          spaceLength,
          stack,
          steppedLine,
          targets,
          thresholds,
          title,
          tooltip,
          type,
          xAxis,
          yAxes,
          yAxis);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public DashboardPanel(
      @JsonProperty("aliasColors") final Map<String, String> aliasColors,
      @JsonProperty("bars") final boolean bars,
      @JsonProperty("dashLength") final int dashLength,
      @JsonProperty("dashes") final boolean dashes,
      @JsonProperty("datasource") final String datasource,
      @JsonProperty("fieldConfig") final FieldConfig fieldConfig,
      @JsonProperty("fill") final int fill,
      @JsonProperty("fillGradient") final int fillGradient,
      @JsonProperty("gridPos") final GridPos gridPos,
      @JsonProperty("hiddenSeries") final boolean hiddenSeries,
      @JsonProperty("id") final int id,
      @JsonProperty("legend") final PanelLegend legend,
      @JsonProperty("lines") final boolean lines,
      @JsonProperty("linewidth") final int lineWidth,
      @JsonProperty("nullPointMode") final String nullPointMode,
      @JsonProperty("percentage") final boolean percentage,
      @JsonProperty("pointRadius") final int pointRadius,
      @JsonProperty("points") final boolean points,
      @JsonProperty("renderer") final String renderer,
      @JsonProperty("seriesOverrides") final List<String> seriesOverrides,
      @JsonProperty("spaceLength") final int spaceLength,
      @JsonProperty("stack") final boolean stack,
      @JsonProperty("steppedLine") final boolean steppedLine,
      @JsonProperty("targets") final List<PanelTarget> targets,
      @JsonProperty("threshold") final List<String> thresholds,
      @JsonProperty("title") final String title,
      @JsonProperty("tooltip") final PanelTooltip tooltip,
      @JsonProperty("type") final String type,
      @JsonProperty("xaxis") final XAxis xAxis,
      @JsonProperty("yaxes") final List<YAxisFormat> yAxes,
      @JsonProperty("yaxis") final YAxis yAxis) {
    this.aliasColors = aliasColors;
    this.bars = bars;
    this.dashLength = dashLength;
    this.dashes = dashes;
    this.datasource = datasource;
    this.fieldConfig = fieldConfig;
    this.fill = fill;
    this.fillGradient = fillGradient;
    this.gridPos = gridPos;
    this.hiddenSeries = hiddenSeries;
    this.id = id;
    this.legend = legend;
    this.lines = lines;
    this.lineWidth = lineWidth;
    this.nullPointMode = nullPointMode;
    this.percentage = percentage;
    this.pointRadius = pointRadius;
    this.points = points;
    this.renderer = renderer;
    this.seriesOverrides = seriesOverrides;
    this.spaceLength = spaceLength;
    this.stack = stack;
    this.steppedLine = steppedLine;
    this.targets = targets;
    this.thresholds = thresholds;
    this.title = title;
    this.tooltip = tooltip;
    this.type = type;
    this.xAxis = xAxis;
    this.yAxes = yAxes;
    this.yAxis = yAxis;
  }

  @JsonProperty("aliasColors")
  public Map<String, String> aliasColors() {
    return aliasColors;
  }

  @JsonProperty("bars")
  public boolean bars() {
    return bars;
  }

  @JsonProperty("dashLength")
  public int dashLength() {
    return dashLength;
  }

  @JsonProperty("dashes")
  public boolean dashes() {
    return dashes;
  }

  @JsonProperty("datasource")
  public String datasource() {
    return datasource;
  }

  @JsonProperty("fieldConfig")
  public FieldConfig fieldConfig() {
    return fieldConfig;
  }

  @JsonProperty("fill")
  public int fill() {
    return fill;
  }

  @JsonProperty("fillGradient")
  public int fillGradient() {
    return fillGradient;
  }

  @JsonProperty("gridPos")
  public GridPos gridPos() {
    return gridPos;
  }

  @JsonProperty("hiddenSeries")
  public boolean hiddenSeries() {
    return hiddenSeries;
  }

  @JsonProperty("id")
  public int id() {
    return id;
  }

  @JsonProperty("legend")
  public PanelLegend legend() {
    return legend;
  }

  @JsonProperty("lines")
  public boolean lines() {
    return lines;
  }

  @JsonProperty("linewidth")
  public int lineWidth() {
    return lineWidth;
  }

  @JsonProperty("nullPointMode")
  public String nullPointMode() {
    return nullPointMode;
  }

  @JsonProperty("percentage")
  public boolean percentage() {
    return percentage;
  }

  @JsonProperty("pointRadius")
  public int pointRadius() {
    return pointRadius;
  }

  @JsonProperty("points")
  public boolean points() {
    return points;
  }

  @JsonProperty("renderer")
  public String renderer() {
    return renderer;
  }

  @JsonProperty("seriesOverrides")
  public List<String> seriesOverrides() {
    return seriesOverrides;
  }

  @JsonProperty("spaceLength")
  public int spaceLength() {
    return spaceLength;
  }

  @JsonProperty("stack")
  public boolean stack() {
    return stack;
  }

  @JsonProperty("steppedLine")
  public boolean steppedLine() {
    return steppedLine;
  }

  @JsonProperty("targets")
  public List<PanelTarget> targets() {
    return targets;
  }

  @JsonProperty("threshold")
  public List<String> thresholds() {
    return thresholds;
  }

  @JsonProperty("title")
  public String title() {
    return title;
  }

  @JsonProperty("tooltip")
  public PanelTooltip tooltip() {
    return tooltip;
  }

  @JsonProperty("type")
  public String type() {
    return type;
  }

  @JsonProperty("xaxis")
  public XAxis xAxis() {
    return xAxis;
  }

  @JsonProperty("yaxes")
  public List<YAxisFormat> yAxes() {
    return yAxes;
  }

  @JsonProperty("yaxis")
  public YAxis yAxis() {
    return yAxis;
  }
}
