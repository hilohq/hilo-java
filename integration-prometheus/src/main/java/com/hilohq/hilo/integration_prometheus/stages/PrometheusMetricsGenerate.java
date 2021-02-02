package com.hilohq.hilo.integration_prometheus.stages;

import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.protobufs.integrations.grafana.Grafana;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public final class PrometheusMetricsGenerate
    extends RichGroupReduceFunction<Fields<FieldKey>, Prometheus.PrometheusEvent> {
  private final String mongoUrl;
  private final String schemaId;
  private Map<String, FeatureOuterClass.Feature> features;

  public PrometheusMetricsGenerate(final String mongoUrl, final String schemaId) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final var store = MongoFeaturesStore.open(mongoUrl);
    final var featuresModel = store.getFeatures(schemaId).get(5, TimeUnit.SECONDS);
    features = featuresModel.getFeaturesMap();
    store.close();
  }

  private static String getExprForFeature(
      final String key, final FeatureOuterClass.Feature feature) {
    if (feature.getFeatureClass().hasMonotonic() || feature.getFeatureClass().hasCounter()) {
      return "irate(" + key + "[1m])";
    }

    return key;
  }

  @Override
  public void reduce(
      final Iterable<Fields<FieldKey>> values, final Collector<Prometheus.PrometheusEvent> out)
      throws Exception {
    final var metrics = new HashMap<String, Prometheus.PrometheusMetric>();
    final var metadataBuilder = Prometheus.PrometheusMetadata.newBuilder();

    for (final var value : values) {
      metadataBuilder
          .setProjectId(value.metadata().getProjectId())
          .setSchemaId(value.metadata().getSchemaId())
          .setSourceId(value.metadata().getSourceId());

      for (final var field : value.map().entrySet()) {
        if (!features.containsKey(field.getKey().key())) {
          continue;
        }

        final var feature = features.get(field.getKey().key());
        if (!feature.getFeatureClass().hasGauge()
            && !feature.getFeatureClass().hasMonotonic()
            && !feature.getFeatureClass().hasCounter()
            && !feature.getFeatureClass().hasDiscrete()) {
          continue;
        }

        if (!metrics.containsKey(field.getKey().key())) {
          final var expr = getExprForFeature(field.getKey().key(), feature);
          metrics.put(
              field.getKey().key(),
              Prometheus.PrometheusMetric.newBuilder()
                  .setKey(field.getKey().key())
                  .setGauge(
                      Prometheus.PrometheusMetric.Gauge.newBuilder()
                          .setName(field.getKey().key())
                          .setHelp(field.getKey().key()))
                  .setDashboard(
                      Prometheus.DashboardIntegrations.newBuilder()
                          .setGrafana(Grafana.GrafanaPanel.newBuilder().setExpr(expr)))
                  .build());
        }
      }
    }

    final var metadata = metadataBuilder.build();
    for (final var metric : metrics.values()) {
      out.collect(
          Prometheus.PrometheusEvent.newBuilder().setMetadata(metadata).setMetric(metric).build());
    }
  }
}
