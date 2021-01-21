package com.hilohq.hilo.integration_influxdb.output;

import com.hilohq.hilo.integration_influxdb.client.InfluxDbClient;
import com.influxdb.client.write.Point;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;

public final class InfluxDbOutputFormat extends RichOutputFormat<Point> {

  private static final String INFLUX_TOKEN = "token";
  private static final String INFLUX_ORG = "org";
  private static final String INFLUX_BUCKET = "db0";

  private final String influxDbUrl;
  private InfluxDbClient influxDbClient;

  public InfluxDbOutputFormat(final String influxDbUrl) {
    this.influxDbUrl = influxDbUrl;
  }

  @Override
  public void configure(Configuration parameters) {}

  @Override
  public void open(int taskNumber, int numTasks) throws IOException {
    this.influxDbClient =
        InfluxDbClient.newBuilder()
            .address(influxDbUrl)
            .bucket(INFLUX_BUCKET)
            .token(INFLUX_TOKEN)
            .org(INFLUX_ORG)
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
