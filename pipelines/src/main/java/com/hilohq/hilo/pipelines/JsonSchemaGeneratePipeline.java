package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.connectors_mongo.sink.MongoOutputFormat;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.hilohq.hilo.stages.JsonSchemaGenerateStages;
import com.hilohq.hilo.structure_learning.schema.mongo.SchemaUpdateMongoCommandGenerator;
import java.util.UUID;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.Path;

public final class JsonSchemaGeneratePipeline
    extends DefaultBatchPipeline<String, Schema.SchemaUpdate> {
  public static final class Builder {
    private String inputPath;
    private String mongoUrl;
    private String database;
    private String projectId = UUID.randomUUID().toString();
    private String sourceId = UUID.randomUUID().toString();
    private String schemaId = UUID.randomUUID().toString();

    public Builder inputPath(final String inputPath) {
      verifyNotNull(inputPath);
      this.inputPath = inputPath;
      return this;
    }

    public Builder projectId(final String projectId) {
      verifyNotNull(projectId);
      this.projectId = projectId;
      return this;
    }

    public Builder sourceId(final String sourceId) {
      verifyNotNull(sourceId);
      this.sourceId = sourceId;
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

    public JsonSchemaGeneratePipeline build() {
      verifyNotNull(inputPath);
      verifyNotNull(mongoUrl);
      verifyNotNull(database);
      verifyNotNull(projectId);
      verifyNotNull(sourceId);
      verifyNotNull(schemaId);

      final var inputFormat = new TextInputFormat(new Path(inputPath));
      final var stages =
          JsonSchemaGenerateStages.newBuilder()
              .projectId(projectId)
              .sourceId(sourceId)
              .schemaId(schemaId)
              .build();
      final var output =
          MongoOutputFormat.newBuilder(new SchemaUpdateMongoCommandGenerator())
              .url(mongoUrl)
              .database(database)
              .build();
      return new JsonSchemaGeneratePipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JsonSchemaGeneratePipeline(
      final FileInputFormat<String> input,
      final JsonSchemaGenerateStages stages,
      final OutputFormat<Schema.SchemaUpdate> output) {
    super(input, stages, output);
  }
}
