package com.hilohq.hilo.integration_grafana.output;

import com.hilohq.hilo.integration_grafana.client.GrafanaClient;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboard;
import com.hilohq.hilo.integration_grafana.entity.PostDashboardRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;

public final class GrafanaOutputFormat extends RichOutputFormat<GrafanaDashboard> {

  private final String grafanaUrl;
  private final String bearerToken;
  private GrafanaClient grafanaClient;

  public GrafanaOutputFormat(final String grafanaUrl, final String bearerToken) {
    this.grafanaUrl = grafanaUrl;
    this.bearerToken = bearerToken;
  }

  @Override
  public void configure(Configuration parameters) {}

  @Override
  public void open(int taskNumber, int numTasks) throws IOException {
    this.grafanaClient =
        GrafanaClient.newBuilder().url(grafanaUrl).bearerToken(bearerToken).build();
  }

  @Override
  public void writeRecord(GrafanaDashboard record) throws IOException {
    try {
      System.out.println("WRITE RECORD " + record);
      grafanaClient
          .postDashboard(
              PostDashboardRequest.newBuilder()
                  .overwrite(true)
                  .folderId(0)
                  .dashboard(record)
                  .build())
          .get(5, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void close() {
    grafanaClient.close();
  }
}
