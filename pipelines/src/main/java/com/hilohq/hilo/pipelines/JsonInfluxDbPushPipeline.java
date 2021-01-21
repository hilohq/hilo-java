package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.integration_influxdb.output.InfluxDbOutputFormat;
import com.hilohq.hilo.stages.JsonInfluxDbPushStages;
import com.influxdb.client.write.Point;
import java.util.UUID;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;

public final class JsonInfluxDbPushPipeline
    extends DefaultBatchPipeline<String, FileInputSplit, Point> {
  public static final class Builder {
    private String inputPath;
    private String mongoUrl;
    private String schemaId = UUID.randomUUID().toString();
    private String influxDbAddress;

    public Builder inputPath(final String inputPath) {
      verifyNotNull(inputPath);
      this.inputPath = inputPath;
      return this;
    }

    public Builder influxDbAddress(final String influxDbAddress) {
      verifyNotNull(influxDbAddress);
      this.influxDbAddress = influxDbAddress;
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

    public JsonInfluxDbPushPipeline build() {
      verifyNotNull(inputPath);
      verifyNotNull(mongoUrl);
      verifyNotNull(schemaId);
      verifyNotNull(influxDbAddress);

      final var inputFormat = new TextInputFormat(new Path(inputPath));
      final var stages =
          JsonInfluxDbPushStages.newBuilder().schemaId(schemaId).mongoUrl(mongoUrl).build();
      final var output = new InfluxDbOutputFormat(influxDbAddress);
      return new JsonInfluxDbPushPipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JsonInfluxDbPushPipeline(
      final FileInputFormat<String> input,
      final JsonInfluxDbPushStages stages,
      final OutputFormat<Point> sink) {
    super(input, stages, sink);
  }
}
