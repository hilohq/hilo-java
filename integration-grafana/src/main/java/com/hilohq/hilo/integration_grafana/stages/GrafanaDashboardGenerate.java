package com.hilohq.hilo.integration_grafana.stages;

import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboard;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboardPanel;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboardPanelUtils;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboardUtils;
import com.hilohq.hilo.protobufs.integrations.grafana.Grafana;
import java.util.ArrayList;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

public final class GrafanaDashboardGenerate
    extends RichGroupReduceFunction<Grafana.GrafanaDashboard, GrafanaDashboard> {

  @Override
  public void reduce(
      final Iterable<Grafana.GrafanaDashboard> values, final Collector<GrafanaDashboard> out)
      throws Exception {
    for (final var dashboard : values) {
      final var dashboardBuilder = GrafanaDashboardUtils.withDefaults().title(dashboard.getTitle());
      final var panels = new ArrayList<GrafanaDashboardPanel>();

      for (final var panel : dashboard.getPanelsList()) {
        panels.add(
            GrafanaDashboardPanelUtils.withDefaults()
                .id(panels.size())
                .target(
                    GrafanaDashboardPanelUtils.targetWithDefaults().expr(panel.getExpr()).build())
                .build());
      }

      dashboardBuilder.panels(panels);
      out.collect(dashboardBuilder.build());
    }
  }
}
