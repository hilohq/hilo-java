package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.connectors_mongo.sink.MongoOutputFormat;
import com.hilohq.hilo.integration_prometheus.converter.PrometheusMetricUpdateMongoCommandGenerator;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.stages.PrometheusGenerateMetricsStages;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.Path;

public final class PrometheusGenerateMetricsPipeline
    extends DefaultBatchPipeline<String, Prometheus.PrometheusUpdate> {
  public static final class Builder {
    private String inputPath;
    private String mongoUrl;
    private String database;
    private String schemaId;

    public Builder inputPath(final String inputPath) {
      verifyNotNull(inputPath);
      this.inputPath = inputPath;
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

    public PrometheusGenerateMetricsPipeline build() {
      verifyNotNull(inputPath);
      verifyNotNull(mongoUrl);
      verifyNotNull(database);
      verifyNotNull(schemaId);

      final var inputFormat = new TextInputFormat(new Path(inputPath));
      final var stages =
          PrometheusGenerateMetricsStages.newBuilder()
              .schemaId(schemaId)
              .mongoUrl(mongoUrl)
              .build();
      final var output =
          MongoOutputFormat.newBuilder(new PrometheusMetricUpdateMongoCommandGenerator())
              .url(mongoUrl)
              .database(database)
              .build();
      return new PrometheusGenerateMetricsPipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  PrometheusGenerateMetricsPipeline(
      final FileInputFormat<String> input,
      final PrometheusGenerateMetricsStages stages,
      final OutputFormat<Prometheus.PrometheusUpdate> output) {
    super(input, stages, output);
  }
}
