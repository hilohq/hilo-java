package com.hilohq.hilo.integration_prometheus.stages;

import com.hilohq.hilo.protobufs.integrations.grafana.Grafana;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import java.util.ArrayList;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

public class PrometheusGrafanaDashboardGenerate
    extends RichGroupReduceFunction<Prometheus.PrometheusMetricsModel, Grafana.GrafanaDashboard> {
  @Override
  public void reduce(
      final Iterable<Prometheus.PrometheusMetricsModel> values,
      final Collector<Grafana.GrafanaDashboard> out)
      throws Exception {
    for (final var value : values) {
      final var panels = new ArrayList<Grafana.GrafanaPanel>();
      for (final var metric : value.getMetricsMap().values()) {
        if (metric.hasDashboard() && metric.getDashboard().hasGrafana()) {
          final var panel = metric.getDashboard().getGrafana();
          panels.add(panel);
        }
      }

      if (panels.size() > 0) {
        out.collect(
            Grafana.GrafanaDashboard.newBuilder()
                .setTitle(value.getMetadata().getSchemaId())
                .addAllPanels(panels)
                .build());
      }
    }
  }
}
