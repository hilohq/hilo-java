package com.hilohq.hilo.integration_influxdb.output;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.integration_influxdb.client.InfluxDbClient;
import com.influxdb.client.write.Point;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;

public final class InfluxDbOutputFormat extends RichOutputFormat<Point> {

  private final String influxDbUrl;
  private final String token;
  private final String org;
  private final String bucket;
  private InfluxDbClient influxDbClient;

  /**
   * Token, organization and bucket come from {@code HILO_INFLUXDB_TOKEN}, {@code HILO_INFLUXDB_ORG}
   * and {@code HILO_INFLUXDB_BUCKET}, read where the pipeline is built.
   */
  public InfluxDbOutputFormat(final String influxDbUrl) {
    this(influxDbUrl, HiloConfig.fromEnvironment());
  }

  public InfluxDbOutputFormat(final String influxDbUrl, final HiloConfig config) {
    this(
        influxDbUrl,
        config.get(HiloConfig.Setting.INFLUXDB_TOKEN),
        config.get(HiloConfig.Setting.INFLUXDB_ORG),
        config.get(HiloConfig.Setting.INFLUXDB_BUCKET));
  }

  public InfluxDbOutputFormat(
      final String influxDbUrl, final String token, final String org, final String bucket) {
    this.influxDbUrl = influxDbUrl;
    this.token = token;
    this.org = org;
    this.bucket = bucket;
  }

  @Override
  public void configure(Configuration parameters) {}

  @Override
  public void open(int taskNumber, int numTasks) throws IOException {
    this.influxDbClient =
        InfluxDbClient.newBuilder()
            .address(influxDbUrl)
            .bucket(bucket)
            .token(token)
            .org(org)
            .build();
  }

  @Override
  public void writeRecord(Point record) throws IOException {
    try {
      influxDbClient.send(record).get(5, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void close() throws IOException {
    influxDbClient.close();
  }
}
