package com.hilohq.hilo.pipelines;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.connectors_mongo.output.MongoOutputFormat;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.hilohq.hilo.stages.JsonFeaturesGenerateStages;
import com.hilohq.hilo.structure_learning.features.mongo.FeaturesUpdateMongoCommandGenerator;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;

public final class JsonFeaturesGeneratePipeline
    extends DefaultBatchPipeline<String, FileInputSplit, FeatureOuterClass.FeaturesUpdate> {
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

    public JsonFeaturesGeneratePipeline build() {
      verifyNotNull(inputPath);
      verifyNotNull(mongoUrl);
      verifyNotNull(database);
      verifyNotNull(schemaId);

      final var inputFormat = new TextInputFormat(new Path(inputPath));
      final var stages =
          JsonFeaturesGenerateStages.newBuilder()
              .schemaId(schemaId)
              .mongoUrl(mongoUrl)
              .database(database)
              .build();
      final var output =
          MongoOutputFormat.newBuilder(new FeaturesUpdateMongoCommandGenerator())
              .url(mongoUrl)
              .database(database)
              .build();
      return new JsonFeaturesGeneratePipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JsonFeaturesGeneratePipeline(
      final FileInputFormat<String> input,
      final JsonFeaturesGenerateStages stages,
      final OutputFormat<FeatureOuterClass.FeaturesUpdate> output) {
    super(input, stages, output);
  }
}
