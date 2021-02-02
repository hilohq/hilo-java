package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.connectors_mongo.output.MongoOutputFormat;
import com.hilohq.hilo.integration_prometheus.stages.PrometheusMetricUpdateMongoCommandGenerator;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.stages.PrometheusMetricsGenerateStages;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;

public final class PrometheusMetricsGeneratePipeline
    extends DefaultBatchPipeline<String, FileInputSplit, Prometheus.PrometheusEvent> {
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

    public PrometheusMetricsGeneratePipeline build() {
      verifyNotNull(inputPath);
      verifyNotNull(mongoUrl);
      verifyNotNull(database);
      verifyNotNull(schemaId);

      final var inputFormat = new TextInputFormat(new Path(inputPath));
      final var stages =
          PrometheusMetricsGenerateStages.newBuilder()
              .schemaId(schemaId)
              .mongoUrl(mongoUrl)
              .build();
      final var output =
          MongoOutputFormat.newBuilder(new PrometheusMetricUpdateMongoCommandGenerator())
              .url(mongoUrl)
              .database(database)
              .build();
      return new PrometheusMetricsGeneratePipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  PrometheusMetricsGeneratePipeline(
      final FileInputFormat<String> input,
      final PrometheusMetricsGenerateStages stages,
      final OutputFormat<Prometheus.PrometheusEvent> output) {
    super(input, stages, output);
  }
}
