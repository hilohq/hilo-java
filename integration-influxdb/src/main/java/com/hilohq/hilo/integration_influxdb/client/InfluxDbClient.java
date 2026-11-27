package com.hilohq.hilo.integration_influxdb.client;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.config.HiloConfig;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.write.Point;
import java.util.concurrent.CompletableFuture;

public final class InfluxDbClient {

  private final InfluxDBClient client;

  public static class Builder {
    private String address = HiloConfig.fromEnvironment().get(HiloConfig.Setting.INFLUXDB_URL);
    private String bucket;
    private String token;
    private String org;

    public Builder address(final String address) {
      verifyNotNull(address);
      this.address = address;
      return this;
    }

    public Builder token(final String token) {
      verifyNotNull(token);
      this.token = token;
      return this;
    }

    public Builder org(final String org) {
      verifyNotNull(org);
      this.org = org;
      return this;
    }

    public Builder bucket(final String bucket) {
      verifyNotNull(bucket);
      this.bucket = bucket;
      return this;
    }

    public InfluxDbClient build() {
      verifyNotNull(address);
      verifyNotNull(token);
      verifyNotNull(org);
      verifyNotNull(bucket);

      return new InfluxDbClient(address, token, org, bucket);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  InfluxDbClient(final String address, final String token, final String org, final String bucket) {
    this(InfluxDBClientFactory.create(address, token.toCharArray(), org, bucket));
  }

  InfluxDbClient(final InfluxDBClient client) {
    this.client = client;
  }

  public CompletableFuture<Void> send(final Point point) {
    final var writeApi = client.getWriteApi();
    writeApi.writePoint(point);
    return CompletableFuture.completedFuture(null);
  }

  public void close() {
    client.close();
  }
}
