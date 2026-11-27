package com.hilohq.hilo.integration_grafana.stages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboard;
import com.hilohq.hilo.integration_grafana.entity.PostDashboardRequest;
import com.hilohq.hilo.protobufs.integrations.grafana.Grafana;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Test;

/**
 * Tests the conversion from the protobuf dashboard model to the Grafana dashboard JSON that
 * GrafanaClient posts to /api/dashboards/db.
 */
public final class GrafanaDashboardGenerateTest {

  private static GrafanaDashboard generate(final Grafana.GrafanaDashboard input) throws Exception {
    final var out = new ArrayList<GrafanaDashboard>();
    new GrafanaDashboardGenerate().reduce(List.of(input), new ListCollector<>(out));
    assertEquals(1, out.size());
    return out.get(0);
  }

  private static JsonNode toJson(final Object value) {
    // same configuration as GrafanaClient
    final var mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    return mapper.valueToTree(value);
  }

  private static Grafana.GrafanaDashboard input() {
    return Grafana.GrafanaDashboard.newBuilder()
        .setTitle("blocks-schema")
        .addPanels(Grafana.GrafanaPanel.newBuilder().setExpr("irate(number[1m])"))
        .addPanels(Grafana.GrafanaPanel.newBuilder().setExpr("gasUsed"))
        .build();
  }

  @Test
  public void testCreatesOnePrometheusGraphPanelPerInputPanel() throws Exception {
    final var dashboard = generate(input());

    assertEquals("blocks-schema", dashboard.title());
    assertEquals(2, dashboard.panels().size());

    final var first = dashboard.panels().get(0);
    assertEquals(0, first.id());
    assertEquals("Prometheus", first.datasource());
    assertEquals("graph", first.type());
    assertEquals(1, first.targets().size());
    assertEquals("irate(number[1m])", first.targets().get(0).expr());
    assertEquals("A", first.targets().get(0).refId());

    final var second = dashboard.panels().get(1);
    assertEquals(1, second.id());
    assertEquals("gasUsed", second.targets().get(0).expr());
  }

  @Test
  public void testDashboardSerializesToGrafanaJson() throws Exception {
    final var json = toJson(generate(input()));

    assertEquals("blocks-schema", json.get("title").asText());
    assertFalse(json.get("editable").asBoolean());
    assertEquals("dark", json.get("style").asText());
    assertEquals("now-30m", json.get("time").get("from").asText());
    assertEquals("now", json.get("time").get("to").asText());
    // Grafana expects lower-case "timepicker"
    assertTrue(json.has("timepicker"));
    assertEquals(
        "Annotations & Alerts", json.get("annotations").get("list").get(0).get("name").asText());

    final var panels = json.get("panels");
    assertEquals(2, panels.size());
    final var panel = panels.get(0);
    assertEquals("Prometheus", panel.get("datasource").asText());
    assertEquals("graph", panel.get("type").asText());
    assertEquals("irate(number[1m])", panel.get("targets").get(0).get("expr").asText());
    assertEquals(9, panel.get("gridPos").get("h").asInt());
    assertEquals(12, panel.get("gridPos").get("w").asInt());
    assertTrue(panel.has("xaxis"));
    assertEquals(2, panel.get("yaxes").size());
  }

  @Test
  public void testPostRequestWrapsTheDashboard() throws Exception {
    final var request =
        PostDashboardRequest.newBuilder()
            .overwrite(true)
            .folderId(0)
            .dashboard(generate(input()))
            .build();

    final var json = toJson(request);

    assertTrue(json.get("overwrite").asBoolean());
    assertEquals(0, json.get("folderId").asInt());
    assertEquals("blocks-schema", json.get("dashboard").get("title").asText());
  }
}
