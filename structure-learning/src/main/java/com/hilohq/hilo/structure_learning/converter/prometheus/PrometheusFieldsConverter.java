package com.hilohq.hilo.structure_learning.converter.prometheus;

import com.hilohq.hilo.features_service.service.MongoFeaturesStore;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.string.StringParser;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.connectors.prometheus.Prometheus;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public final class PrometheusFieldsConverter
    extends ProcessFunction<Fields<FieldKey>, Collection<Prometheus.PrometheusValue>> {

  private final String mongoUrl;
  private final String schemaId;
  private final StringParser parser;
  private Map<String, FeatureOuterClass.Feature> features;

  public PrometheusFieldsConverter(final String mongoUrl, final String schemaId) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
    this.parser = new StringParser();
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final var store = MongoFeaturesStore.open(mongoUrl);
    final var featuresModel = store.getFeatures(schemaId).get(5, TimeUnit.SECONDS);
    features = featuresModel.getFeaturesMap();
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
      final Collector<Collection<Prometheus.PrometheusValue>> out)
      throws Exception {
    final var result = new HashSet<Prometheus.PrometheusValue>();
    for (final var entry : fields.map().entrySet()) {
      if (!features.containsKey(entry.getKey().key())) {
        continue;
      }

      final var key = entry.getKey().key();
      final var feature = features.get(entry.getKey().key());
      if (!feature.getFeatureClass().hasGauge()
          && !feature.getFeatureClass().hasCounter()
          && !feature.getFeatureClass().hasMonotonic()
          && !feature.getFeatureClass().hasDiscrete()) {
        continue;
      }

      final var value = parseValue(entry.getValue());
      if (!value.isNumeric()) {
        continue;
      }

      if (feature.getFeatureClass().hasGauge()
          || feature.getFeatureClass().hasCounter()
          || feature.getFeatureClass().hasMonotonic()) {
        final var gaugeValue =
            Prometheus.PrometheusValue.Gauge.newBuilder()
                .setName(key)
                .setValue(value.numericValue())
                .build();
        result.add(Prometheus.PrometheusValue.newBuilder().setGauge(gaugeValue).build());
      } else {
        final var histogramValue =
            Prometheus.PrometheusValue.Histogram.newBuilder()
                .setName(key)
                .setValue(value.numericValue())
                .build();
        result.add(Prometheus.PrometheusValue.newBuilder().setHistogram(histogramValue).build());
      }
    }

    out.collect(result);
  }
}
