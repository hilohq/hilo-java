package com.hilohq.hilo.integration_grafana.entity;

import java.util.Arrays;

public final class GrafanaDashboardPanelUtils {
  private GrafanaDashboardPanelUtils() {}

  public static PanelTarget.Builder targetWithDefaults() {
    return PanelTarget.newBuilder()
        .expr("irate(number[1m])")
        .interval("")
        .legendFormat("")
        .refId("A");
  }

  public static GrafanaDashboardPanel.Builder withDefaults() {
    return GrafanaDashboardPanel.newBuilder()
        .bars(false)
        .dashLength(10)
        .dashes(false)
        .datasource("Prometheus")
        .fieldConfig(
            FieldConfig.newBuilder()
                .defaults(new FieldConfig.Defaults(new FieldConfig.Defaults.Custom()))
                .build())
        .fill(1)
        .fillGradient(0)
        .gridPos(GridPos.newBuilder().h(9).w(12).x(0).y(40).build())
        .hiddenSeries(false)
        .legend(PanelLegend.newBuilder().show(true).build())
        .lines(true)
        .lineWidth(1)
        .nullPointMode("null")
        .percentage(false)
        .pointRadius(2)
        .points(false)
        .renderer("flot")
        .spaceLength(10)
        .stack(false)
        .steppedLine(false)
        .tooltip(PanelTooltip.newBuilder().shared(true).sort(0).valueType("individual").build())
        .type("graph")
        .xAxis(XAxis.newBuilder().mode("time").show(true).build())
        .yAxis(YAxis.newBuilder().align(false).build())
        .yAxes(
            Arrays.asList(
                YAxisFormat.newBuilder().format("short").logBase(1).show(true).build(),
                YAxisFormat.newBuilder().format("short").logBase(1).show(true).build()));
  }
}
