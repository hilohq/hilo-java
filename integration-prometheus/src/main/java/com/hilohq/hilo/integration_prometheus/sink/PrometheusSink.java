package com.hilohq.hilo.integration_prometheus.sink;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.integration_prometheus.client.PrometheusClient;
import com.hilohq.hilo.integrations_service.service.MongoIntegrationsStore;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import java.util.concurrent.TimeUnit;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public final class PrometheusSink extends RichSinkFunction<Prometheus.PrometheusValues> {

  private final String prometheusGatewayAddress;
  private final String tag;
  private final String mongoUrl;
  private final String schemaId;
  private PrometheusClient client;

  public static class Builder {
    private String address = "127.0.0.1:9091";
    private String tag = "default";
    private String mongoUrl = "localhost";
    private String schemaId = null;

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

    public Builder mongoUrl(final String mongoUrl) {
      verifyNotNull(tag);
      this.mongoUrl = mongoUrl;
      return this;
    }

    public Builder schemaId(final String schemaId) {
      verifyNotNull(tag);
      this.schemaId = schemaId;
      return this;
    }

    public PrometheusSink build() {
      return new PrometheusSink(address, tag, mongoUrl, schemaId);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  PrometheusSink(
      final String prometheusGatewayAddress,
      final String tag,
      final String mongoUrl,
      final String schemaId) {
    verifyNotNull(prometheusGatewayAddress);
    verifyNotNull(tag);
    verifyNotNull(mongoUrl);
    verifyNotNull(schemaId);

    this.prometheusGatewayAddress = prometheusGatewayAddress;
    this.tag = tag;
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    client = PrometheusClient.newBuilder().address(prometheusGatewayAddress).tag(tag).build();

    final var store = MongoIntegrationsStore.open(mongoUrl);
    final var metricsModel = store.getPrometheusMetrics(schemaId).get(5, TimeUnit.SECONDS);
    store.close();

    for (final var metric : metricsModel.getMetricsMap().values()) {
      client.register(metric);
    }
  }

  @Override
  public void invoke(final Prometheus.PrometheusValues values, final Context context)
      throws Exception {
    Thread.sleep(500);
    client.send(values);
  }

  @Override
  public void close() {
    client.close();
  }
}
