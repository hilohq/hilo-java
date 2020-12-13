package com.hilohq.hilo.integration_grafana.stages;

import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboard;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboardPanel;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboardPanelUtils;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboardUtils;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass.FeaturesModel;
import java.util.ArrayList;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.util.Collector;

public final class GrafanaDashboardGenerate
    extends RichGroupReduceFunction<FeaturesModel, GrafanaDashboard> {

  @Override
  public void reduce(final Iterable<FeaturesModel> values, final Collector<GrafanaDashboard> out)
      throws Exception {
    for (final var features : values) {
      final var dashboardBuilder =
          GrafanaDashboardUtils.withDefaults().title(features.getMetadata().getSchemaId());
      final var panels = new ArrayList<GrafanaDashboardPanel>();
      final var featureMap = features.getFeaturesMap();
      for (final var featureKey : featureMap.keySet()) {
        final var feature = featureMap.get(featureKey);

        if (feature.getFeatureClass().hasGauge() || feature.getFeatureClass().hasDiscrete()) {
          panels.add(
              GrafanaDashboardPanelUtils.withDefaults()
                  .id(panels.size())
                  .target(GrafanaDashboardPanelUtils.targetWithDefaults().expr(featureKey).build())
                  .build());

        } else if (feature.getFeatureClass().hasMonotonic()
            || feature.getFeatureClass().hasCounter()) {
          panels.add(
              GrafanaDashboardPanelUtils.withDefaults()
                  .id(panels.size())
                  .target(
                      GrafanaDashboardPanelUtils.targetWithDefaults()
                          .expr("irate(" + featureKey + "[1m])")
                          .build())
                  .build());
        }
      }

      dashboardBuilder.panels(panels);
      out.collect(dashboardBuilder.build());
    }
  }
}
