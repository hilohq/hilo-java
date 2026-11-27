package com.hilohq.hilo.integration_prometheus.stages;

import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonParser;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Test;

/** Tests turning validated log fields into Prometheus gauge values for known metrics. */
public final class PrometheusValueGenerateTest {

  private static Prometheus.PrometheusMetric gauge(final String key) {
    return Prometheus.PrometheusMetric.newBuilder()
        .setKey(key)
        .setGauge(Prometheus.PrometheusMetric.Gauge.newBuilder().setName(key).setHelp(key))
        .build();
  }

  @Test
  public void testEmitsGaugeValuesOnlyForKnownNumericMetrics() throws Exception {
    final var metrics = new HashMap<String, Prometheus.PrometheusMetric>();
    metrics.put("gas", gauge("gas"));
    metrics.put("gasPrice", gauge("gasPrice"));
    metrics.put("from", gauge("from"));

    final var stage = new PrometheusValueGenerate("unused", "test-schema");
    PrometheusMetricsGenerateTest.inject(stage, "metrics", metrics);

    final var map = new FieldsMap<FieldKey>();
    for (final var entry :
        new JsonParser()
            .parse("{\"gas\":21000,\"gasPrice\":\"80525500000\",\"from\":\"0xabc\",\"nonce\":7}")
            .entrySet()) {
      map.put(entry.getKey(), entry.getValue());
    }

    final var out = new ArrayList<Prometheus.PrometheusValues>();
    stage.processElement(
        new Fields<>(Schema.SchemaMetadata.getDefaultInstance(), map),
        null,
        new ListCollector<>(out));

    assertEquals(1, out.size());
    final var values = new HashMap<String, Double>();
    for (final var value : out.get(0).getValuesList()) {
      assertEquals(value.getKey(), value.getGauge().getName());
      values.put(value.getKey(), value.getGauge().getValue());
    }

    // "from" is not numeric and "nonce" has no metric, so both are dropped
    assertEquals(2, values.size());
    assertEquals(21000.0, values.get("gas"), 0.0);
    // string-encoded numbers are re-parsed before being pushed
    assertEquals(80525500000.0, values.get("gasPrice"), 0.0);
    assertEquals(null, values.get(new JsonFieldKey("from").key()));
  }
}
