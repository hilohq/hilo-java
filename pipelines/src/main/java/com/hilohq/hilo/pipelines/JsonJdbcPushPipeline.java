package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.stages.JsonJdbcPushStages;
import java.util.UUID;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.types.Row;

public class JsonJdbcPushPipeline extends DefaultBatchPipeline<String, FileInputSplit, Row> {
  public static final class Builder {
    private String inputPath;
    private String mongoUrl;
    private String schemaId = UUID.randomUUID().toString();
    private String jdbcDriverName;
    private String jdbcDbUrl;
    private String jdbcUsername;
    private String jdbcPassword;
    private String jdbcQuery;
    private int jdbcBatchInterval = 1;

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

    public Builder jdbcDriverName(final String jdbcDriverName) {
      verifyNotNull(jdbcDriverName);
      this.jdbcDriverName = jdbcDriverName;
      return this;
    }

    public Builder jdbcDbUrl(final String jdbcDbUrl) {
      verifyNotNull(jdbcDbUrl);
      this.jdbcDbUrl = jdbcDbUrl;
      return this;
    }

    public Builder jdbcUsername(final String jdbcUsername) {
      verifyNotNull(jdbcUsername);
      this.jdbcUsername = jdbcUsername;
      return this;
    }

    public Builder jdbcPassword(final String jdbcPassword) {
      verifyNotNull(jdbcPassword);
      this.jdbcPassword = jdbcPassword;
      return this;
    }

    public Builder jdbcQuery(final String jdbcQuery) {
      verifyNotNull(jdbcQuery);
      this.jdbcQuery = jdbcQuery;
      return this;
    }

    public Builder jdbcBatchInterval(final int jdbcBatchInterval) {
      verifyNotNull(jdbcBatchInterval);
      this.jdbcBatchInterval = jdbcBatchInterval;
      return this;
    }

    public JsonJdbcPushPipeline build() {
      verifyNotNull(inputPath);
      verifyNotNull(mongoUrl);
      verifyNotNull(schemaId);
      verifyNotNull(jdbcDriverName);
      verifyNotNull(jdbcDbUrl);
      verifyNotNull(jdbcUsername);
      verifyNotNull(jdbcPassword);
      verifyNotNull(jdbcQuery);

      final var inputFormat = new TextInputFormat(new Path(inputPath));
      final var stages =
          JsonJdbcPushStages.newBuilder()
              .schemaId(schemaId)
              .mongoUrl(mongoUrl)
              .jdbcDbUrl(jdbcDbUrl)
              .jdbcUsername(jdbcUsername)
              .jdbcPassword(jdbcPassword)
              .build();
      final var output =
          JDBCOutputFormat.buildJDBCOutputFormat()
              .setDrivername(jdbcDriverName)
              .setDBUrl(jdbcDbUrl)
              .setUsername(jdbcUsername)
              .setPassword(jdbcPassword)
              .setQuery(jdbcQuery)
              .setBatchInterval(jdbcBatchInterval)
              .finish();

      return new JsonJdbcPushPipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JsonJdbcPushPipeline(
      final FileInputFormat<String> input,
      final JsonJdbcPushStages stages,
      final OutputFormat<Row> sink) {
    super(input, stages, sink);
  }
}
