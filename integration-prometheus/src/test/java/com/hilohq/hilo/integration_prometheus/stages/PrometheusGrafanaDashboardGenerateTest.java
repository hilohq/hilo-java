package com.hilohq.hilo.integration_prometheus.stages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.protobufs.integrations.grafana.Grafana;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Test;

public final class PrometheusGrafanaDashboardGenerateTest {

  private static Prometheus.PrometheusMetric metric(final String key, final String expr) {
    return Prometheus.PrometheusMetric.newBuilder()
        .setKey(key)
        .setGauge(Prometheus.PrometheusMetric.Gauge.newBuilder().setName(key).setHelp(key))
        .setDashboard(
            Prometheus.DashboardIntegrations.newBuilder()
                .setGrafana(Grafana.GrafanaPanel.newBuilder().setExpr(expr)))
        .build();
  }

  private static List<Grafana.GrafanaDashboard> generate(
      final Prometheus.PrometheusMetricsModel model) throws Exception {
    final var out = new ArrayList<Grafana.GrafanaDashboard>();
    new PrometheusGrafanaDashboardGenerate().reduce(List.of(model), new ListCollector<>(out));
    return out;
  }

  @Test
  public void testBuildsOneDashboardPerSchemaWithOnePanelPerMetric() throws Exception {
    final var model =
        Prometheus.PrometheusMetricsModel.newBuilder()
            .setMetadata(Prometheus.PrometheusMetadata.newBuilder().setSchemaId("blocks-schema"))
            .putMetrics("number", metric("number", "irate(number[1m])"))
            .putMetrics("gasUsed", metric("gasUsed", "gasUsed"))
            // metrics without a dashboard integration do not get a panel
            .putMetrics(
                "size",
                Prometheus.PrometheusMetric.newBuilder()
                    .setKey("size")
                    .setGauge(Prometheus.PrometheusMetric.Gauge.newBuilder().setName("size"))
                    .build())
            .build();

    final var dashboards = generate(model);

    assertEquals(1, dashboards.size());
    final var dashboard = dashboards.get(0);
    assertEquals("blocks-schema", dashboard.getTitle());
    final Set<String> exprs = new HashSet<>();
    for (final var panel : dashboard.getPanelsList()) {
      exprs.add(panel.getExpr());
    }
    assertEquals(Set.of("irate(number[1m])", "gasUsed"), exprs);
  }

  @Test
  public void testSkipsModelsWithoutAnyPanels() throws Exception {
    final var model =
        Prometheus.PrometheusMetricsModel.newBuilder()
            .setMetadata(Prometheus.PrometheusMetadata.newBuilder().setSchemaId("empty"))
            .build();

    assertTrue(generate(model).isEmpty());
  }
}
