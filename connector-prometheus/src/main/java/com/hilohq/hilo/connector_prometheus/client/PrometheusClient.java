package com.hilohq.hilo.connector_prometheus.client;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.protobufs.connectors.prometheus.Prometheus;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;

public final class PrometheusClient {

  private final String tag;
  private final PushGateway pushGateway;
  private final CollectorRegistry registry;

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
    this.pushGateway = new PushGateway("127.0.0.1:9091");
    this.registry = new CollectorRegistry();
  }

  public void register(final Collection<Prometheus.PrometheusValue> values) {
    for (final var value : values) {
      register(value);
    }

    try {
      pushGateway.pushAdd(registry, tag);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void register(final Prometheus.PrometheusValue value) {
    if (value.hasGauge()) {
      register(registry, value.getGauge());
    }
  }

  private void register(
      final CollectorRegistry registry, final Prometheus.PrometheusValue.Gauge gauge) {
    final var metric = Gauge.build(gauge.getName(), gauge.getHelp()).create();
    metric.set(gauge.getValue());
    registry.register(metric);
  }

  public void close() {}
}
