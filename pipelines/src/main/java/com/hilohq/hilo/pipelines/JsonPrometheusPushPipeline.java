package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.integration_prometheus.sink.PrometheusSink;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.stages.JsonPrometheusPushStages;
import java.util.UUID;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public final class JsonPrometheusPushPipeline
    extends DefaultStreamPipeline<String, Prometheus.PrometheusValues> {
  public static final class Builder {
    private String inputPath;
    private String mongoUrl;
    private String schemaId = UUID.randomUUID().toString();
    private String prometheusAddress;

    public Builder inputPath(final String inputPath) {
      verifyNotNull(inputPath);
      this.inputPath = inputPath;
      return this;
    }

    public Builder prometheusAddress(final String prometheusAddress) {
      verifyNotNull(prometheusAddress);
      this.prometheusAddress = prometheusAddress;
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

    public JsonPrometheusPushPipeline build() {
      verifyNotNull(inputPath);
      verifyNotNull(mongoUrl);
      verifyNotNull(schemaId);
      verifyNotNull(prometheusAddress);

      final var inputFormat = new TextInputFormat(new Path(inputPath));
      final var stages =
          JsonPrometheusPushStages.newBuilder().schemaId(schemaId).mongoUrl(mongoUrl).build();
      final var output =
          PrometheusSink.newBuilder()
              .schemaId(schemaId)
              .mongoUrl(mongoUrl)
              .address(prometheusAddress)
              .tag("test")
              .build();
      return new JsonPrometheusPushPipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JsonPrometheusPushPipeline(
      final FileInputFormat<String> input,
      final JsonPrometheusPushStages stages,
      final SinkFunction<Prometheus.PrometheusValues> sink) {
    super(input, stages, sink);
  }
}
