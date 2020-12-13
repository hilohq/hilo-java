package com.hilohq.hilo.stages;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.integration_prometheus.stages.PrometheusMetricsGenerate;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaBatchValidate;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.DataSet;

public final class PrometheusMetricsGenerateStages
    implements BatchStages<String, Prometheus.PrometheusUpdate> {
  private final String schemaId;
  private final String mongoUrl;

  public static final class Builder {
    private String schemaId = null;
    private String mongoUrl = "localhost";

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

    public PrometheusMetricsGenerateStages build() {
      return new PrometheusMetricsGenerateStages(schemaId, mongoUrl);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  PrometheusMetricsGenerateStages(final String schemaId, final String mongoUrl) {
    this.schemaId = schemaId;
    this.mongoUrl = mongoUrl;
  }

  @Override
  public DataSet<Prometheus.PrometheusUpdate> bind(
      final ExecutionConfig executionConfig, final DataSet<String> input) throws Exception {
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionConfig.addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);
    final Class<?> type = Class.forName("com.google.protobuf.MapEntry");
    executionConfig.registerTypeWithKryoSerializer(type, ProtobufSerializer.class);

    return input
        .reduceGroup(new JsonSchemaBatchValidate(mongoUrl, schemaId))
        .reduceGroup(new PrometheusMetricsGenerate(mongoUrl, schemaId));
  }
}
