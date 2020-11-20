package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.integration_prometheus.converter.PrometheusFieldsConverter;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaStreamValidate;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public final class JsonPrometheusPushPipelineBuilder
    implements StreamPipelineBuilder<String, Prometheus.PrometheusValues> {
  private final String schemaId;
  private final String mongoUrl;

  public JsonPrometheusPushPipelineBuilder(final String schemaId, final String mongoUrl) {
    this.schemaId = schemaId;
    this.mongoUrl = mongoUrl;
  }

  @Override
  public DataStream<Prometheus.PrometheusValues> build(
      final StreamExecutionEnvironment executionEnvironment, final DataStreamSource<String> input)
      throws Exception {
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionEnvironment
        .getConfig()
        .addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);
    final Class<?> type = Class.forName("com.google.protobuf.MapEntry");
    executionEnvironment.getConfig().registerTypeWithKryoSerializer(type, ProtobufSerializer.class);

    return input
        .process(new JsonSchemaStreamValidate(mongoUrl, schemaId))
        .process(new PrometheusFieldsConverter(mongoUrl, schemaId));
  }
}
