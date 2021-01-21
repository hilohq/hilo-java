package com.hilohq.hilo.pipelines;

import static org.apache.flink.shaded.guava18.com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.connectors_mongo.input.IdInputSplit;
import com.hilohq.hilo.connectors_mongo.input.MongoInputFormat;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboard;
import com.hilohq.hilo.integration_grafana.output.GrafanaOutputFormat;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.stages.PrometheusGrafanaDashboardGenerateStages;
import java.util.Collections;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

public final class PrometheusGrafanaDashboardGeneratePipeline
    extends DefaultBatchPipeline<
        Prometheus.PrometheusMetricsModel, IdInputSplit, GrafanaDashboard> {
  public static final class Builder {
    private String mongoUrl;
    private String database;
    private String schemaId;
    private String grafanaUrl;
    private String bearerToken;

    public Builder grafanaUrl(final String grafanaUrl) {
      verifyNotNull(grafanaUrl);
      this.grafanaUrl = grafanaUrl;
      return this;
    }

    public Builder bearerToken(final String bearerToken) {
      verifyNotNull(bearerToken);
      this.bearerToken = bearerToken;
      return this;
    }

    public Builder schemaId(final String schemaId) {
      verifyNotNull(schemaId);
      this.schemaId = schemaId;
      return this;
    }

    public Builder mongoUrl(final String mongoUrl) {
      verifyNotNull(mongoUrl);
      this.mongoUrl = mongoUrl;
      return this;
    }

    public Builder database(final String database) {
      verifyNotNull(database);
      this.database = database;
      return this;
    }

    public PrometheusGrafanaDashboardGeneratePipeline build() {
      verifyNotNull(grafanaUrl);
      verifyNotNull(bearerToken);
      verifyNotNull(mongoUrl);
      verifyNotNull(database);
      verifyNotNull(schemaId);

      final var inputFormat =
          new MongoInputFormat<>(
              mongoUrl,
              database,
              "prometheus",
              Collections.singletonList(schemaId),
              "metadata.schemaId",
              Prometheus.PrometheusMetricsModel.newBuilder().build());
      final var stages = new PrometheusGrafanaDashboardGenerateStages();
      final var output = new GrafanaOutputFormat(grafanaUrl, bearerToken);
      return new PrometheusGrafanaDashboardGeneratePipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private static class TypeInformationSupplier
      implements com.hilohq.hilo.pipelines.TypeInformationSupplier<
          Prometheus.PrometheusMetricsModel> {

    @Override
    public TypeInformation<Prometheus.PrometheusMetricsModel> of(
        InputFormat<Prometheus.PrometheusMetricsModel, ?> type) {
      if (!type.getClass().equals(MongoInputFormat.class)) {
        throw new IllegalArgumentException("expected MongoInputFormat");
      }

      return TypeInformation.of(new TypeHint<Prometheus.PrometheusMetricsModel>() {});
    }
  }

  PrometheusGrafanaDashboardGeneratePipeline(
      final MongoInputFormat<Prometheus.PrometheusMetricsModel> input,
      final PrometheusGrafanaDashboardGenerateStages stages,
      final OutputFormat<GrafanaDashboard> output) {
    super(input, stages, output, new TypeInformationSupplier());
  }
}
