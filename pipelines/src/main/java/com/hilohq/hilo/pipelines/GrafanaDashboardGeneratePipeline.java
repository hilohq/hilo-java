package com.hilohq.hilo.pipelines;

import static org.apache.flink.shaded.guava18.com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.connectors_mongo.input.IdInputSplit;
import com.hilohq.hilo.connectors_mongo.input.MongoInputFormat;
import com.hilohq.hilo.integration_grafana.entity.GrafanaDashboard;
import com.hilohq.hilo.integration_grafana.output.GrafanaOutputFormat;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass.FeaturesModel;
import com.hilohq.hilo.stages.GrafanaDashboardGenerateStages;
import java.util.Collections;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

public final class GrafanaDashboardGeneratePipeline
    extends DefaultBatchPipeline<FeaturesModel, IdInputSplit, GrafanaDashboard> {
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

    public GrafanaDashboardGeneratePipeline build() {
      verifyNotNull(grafanaUrl);
      verifyNotNull(bearerToken);
      verifyNotNull(mongoUrl);
      verifyNotNull(database);
      verifyNotNull(schemaId);

      final var inputFormat =
          new MongoInputFormat<>(
              mongoUrl,
              database,
              "features",
              Collections.singletonList(schemaId),
              "metadata.schemaId",
              FeaturesModel.newBuilder().build());
      final var stages = new GrafanaDashboardGenerateStages();
      final var output = new GrafanaOutputFormat(grafanaUrl, bearerToken);
      return new GrafanaDashboardGeneratePipeline(inputFormat, stages, output);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private static class TypeInformationSupplier
      implements com.hilohq.hilo.pipelines.TypeInformationSupplier<FeaturesModel> {

    @Override
    public TypeInformation<FeaturesModel> of(InputFormat<FeaturesModel, ?> type) {
      if (!type.getClass().equals(MongoInputFormat.class)) {
        throw new IllegalArgumentException("expected MongoInputFormat");
      }

      return TypeInformation.of(new TypeHint<FeaturesModel>() {});
    }
  }

  GrafanaDashboardGeneratePipeline(
      final MongoInputFormat<FeaturesModel> input,
      final GrafanaDashboardGenerateStages stages,
      final OutputFormat<GrafanaDashboard> output) {
    super(input, stages, output, new TypeInformationSupplier());
  }
}
