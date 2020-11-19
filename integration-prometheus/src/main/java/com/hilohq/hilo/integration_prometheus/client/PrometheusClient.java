package com.hilohq.hilo.integration_prometheus.client;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public final class PrometheusClient {

  private final String tag;
  private final PushGateway pushGateway;
  private final CollectorRegistry registry;
  private final Map<String, Gauge> gaugeMap;

  public static class Builder {
    private String address = "127.0.0.1:9091";
    private String tag = "default";

    public Builder address(final String address) {
      verifyNotNull(address);
      this.address = address;
      return this;
    }

    public Builder tag(final String tag) {
      verifyNotNull(tag);
      this.tag = tag;
      return this;
    }

    public PrometheusClient build() {
      verifyNotNull(address);
      verifyNotNull(tag);
      return new PrometheusClient(address, tag);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  PrometheusClient(final String address, final String tag) {
    this.tag = tag;
    this.pushGateway = new PushGateway(address);
    this.registry = new CollectorRegistry();
    this.gaugeMap = new HashMap<>();
  }

  public void register(final Prometheus.PrometheusMetric metric) {
    if (metric.hasGauge()) {
      final var gauge = metric.getGauge();
      gaugeMap.put(
          metric.getKey(),
          Gauge.build(gauge.getName(), gauge.getHelp())
              .labelNames(gauge.getLabelsList().toArray(new String[gauge.getLabelsCount()]))
              .create());
      registry.register(gaugeMap.get(metric.getKey()));
    }
  }

  public void send(final Prometheus.PrometheusValues values) {
    for (final var value : values.getValuesList()) {
      send(value);
    }

    try {
      pushGateway.pushAdd(registry, tag);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void send(final Prometheus.PrometheusValue value) {
    if (value.hasGauge()) {
      send(value.getKey(), value.getGauge());
    }
  }

  private void send(final String key, final Prometheus.PrometheusValue.Gauge gauge) {
    if (!gaugeMap.containsKey(key)) {
      return;
    }

    final var metric = gaugeMap.get(key);
    metric.set(gauge.getValue());
  }

  public void close() {}
}
