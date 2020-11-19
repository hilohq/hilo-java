package com.hilohq.hilo.integration_prometheus.converter;

import com.hilohq.hilo.integrations_service.service.MongoIntegrationsStore;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.string.StringParser;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public final class PrometheusFieldsConverter
    extends ProcessFunction<Fields<FieldKey>, Prometheus.PrometheusValues> {

  private final String mongoUrl;
  private final String schemaId;
  private final StringParser parser;
  private Map<String, Prometheus.PrometheusMetric> metrics;

  public PrometheusFieldsConverter(final String mongoUrl, final String schemaId) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
    this.parser = new StringParser();
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final var store = MongoIntegrationsStore.open(mongoUrl);
    final var metricsModel = store.getPrometheusMetrics(schemaId).get(5, TimeUnit.SECONDS);
    metrics = metricsModel.getMetricsMap();
    store.close();
  }

  private Value parseValue(final Value value) {
    if (value.isString()) {
      return parser.parse(value.stringValue());
    }
    return value;
  }

  @Override
  public void processElement(
      final Fields<FieldKey> fields,
      final Context ctx,
      final Collector<Prometheus.PrometheusValues> out)
      throws Exception {
    final var result = Prometheus.PrometheusValues.newBuilder();
    for (final var entry : fields.map().entrySet()) {
      if (!metrics.containsKey(entry.getKey().key())) {
        continue;
      }

      final var key = entry.getKey().key();
      final var metric = metrics.get(entry.getKey().key());
      final var value = parseValue(entry.getValue());
      if (!value.isNumeric()) {
        continue;
      }

      if (metric.hasGauge()) {
        final var gaugeValue =
            Prometheus.PrometheusValue.Gauge.newBuilder()
                .setName(key)
                .setValue(value.numericValue())
                .build();
        result.addValues(
            Prometheus.PrometheusValue.newBuilder().setKey(key).setGauge(gaugeValue).build());
      }
    }

    out.collect(result.build());
  }
}
