package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.integration_prometheus.converter.PrometheusFeaturesConverter;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaBatchValidate;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;

public final class PrometheusGenerateMetricsPipelineBuilder
    implements BatchPipelineBuilder<String, Prometheus.PrometheusUpdate> {
  private final String schemaId;
  private final String mongoUrl;

  public PrometheusGenerateMetricsPipelineBuilder(final String schemaId, final String mongoUrl) {
    this.schemaId = schemaId;
    this.mongoUrl = mongoUrl;
  }

  @Override
  public DataSet<Prometheus.PrometheusUpdate> build(
      final ExecutionEnvironment executionEnvironment, final DataSource<String> input)
      throws Exception {
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionEnvironment
        .getConfig()
        .addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);
    final Class<?> type = Class.forName("com.google.protobuf.MapEntry");
    executionEnvironment.getConfig().registerTypeWithKryoSerializer(type, ProtobufSerializer.class);

    return input
        .reduceGroup(new JsonSchemaBatchValidate(mongoUrl, schemaId))
        .reduceGroup(new PrometheusFeaturesConverter(mongoUrl, schemaId));
  }
}
