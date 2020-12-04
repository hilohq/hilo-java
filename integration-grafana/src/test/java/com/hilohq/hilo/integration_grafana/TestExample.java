package com.hilohq.hilo.integration_grafana;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hilohq.hilo.integration_grafana.client.GrafanaClient;
import com.hilohq.hilo.integration_grafana.entity.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public final class TestExample {

  private static DashboardPanel.Builder defaultPanel() {
    return DashboardPanel.newBuilder()
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

  private PostDashboardRequest buildRequest() {
    return PostDashboardRequest.newBuilder()
            .folderId(0)
            .overwrite(true)
            .dashboard(
                    DashboardUtils.withDefaults()
                            .title("example3")
                            .panels(
                                    Arrays.asList(
                                            defaultPanel()
                                                    .id(0)
                                                    .targets(
                                                            Collections.singletonList(
                                                                    PanelTarget.newBuilder()
                                                                            .expr("irate(number[1m])")
                                                                            .interval("")
                                                                            .legendFormat("")
                                                                            .refId("A")
                                                                            .build()))
                                                    .build(),
                                            defaultPanel()
                                                    .id(1)
                                                    .targets(
                                                            Collections.singletonList(
                                                                    PanelTarget.newBuilder()
                                                                            .expr("irate(difficulty[1m])")
                                                                            .interval("")
                                                                            .legendFormat("")
                                                                            .refId("A")
                                                                            .build()))
                                                    .build(),
                                            defaultPanel()
                                                    .id(2)
                                                    .targets(
                                                            Collections.singletonList(
                                                                    PanelTarget.newBuilder()
                                                                            .expr("irate(totalDifficulty[1m])")
                                                                            .interval("")
                                                                            .legendFormat("")
                                                                            .refId("A")
                                                                            .build()))
                                                    .build()))
                            .build())
            .build();
  }

  @Test
  public void testExample() throws Exception {
    final var mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    final var client =
        GrafanaClient.newBuilder()
            .bearerToken(
                "eyJrIjoiamNFWU9aOHpDZUQ3UURBbGFXWGJJUVRSMU91aDdOTlQiLCJuIjoidG1wIiwiaWQiOjF9")
            .build();

    final var request = buildRequest();
    System.out.println(mapper.writeValueAsString(request));
    final var response =
        client
            .postDashboard(request)
            .get();
    System.out.println(mapper.writeValueAsString(response));
  }
}
