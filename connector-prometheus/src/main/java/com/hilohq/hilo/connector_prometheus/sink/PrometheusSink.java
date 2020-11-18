package com.hilohq.hilo.connector_prometheus.sink;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.connector_prometheus.client.PrometheusClient;
import com.hilohq.hilo.protobufs.connectors.prometheus.Prometheus;
import java.util.Collection;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public final class PrometheusSink extends RichSinkFunction<Collection<Prometheus.PrometheusValue>> {

  private final String prometheusGatewayAddress;
  private final String tag;
  private PrometheusClient client;

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

    public PrometheusSink build() {
      verifyNotNull(address);
      verifyNotNull(tag);
      return new PrometheusSink(address, tag);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  PrometheusSink(final String prometheusGatewayAddress, final String tag) {
    this.prometheusGatewayAddress = prometheusGatewayAddress;
    this.tag = tag;
  }

  @Override
  public void open(final Configuration parameters) {
    client = PrometheusClient.newBuilder().address(prometheusGatewayAddress).tag(tag).build();
  }

  @Override
  public void invoke(final Collection<Prometheus.PrometheusValue> value, final Context context)
      throws Exception {
    client.register(value);
  }

  @Override
  public void close() {
    client.close();
  }
}
