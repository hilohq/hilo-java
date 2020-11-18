package com.hilohq.integration_tests.pipelines;

import com.hilohq.hilo.protobufs.connectors.prometheus.Prometheus;
import com.hilohq.hilo.structure_learning.converter.prometheus.PrometheusFieldsConverter;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaStreamValidate;
import java.util.Collection;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public final class JsonPrometheusPushPipelineBuilder
    implements StreamPipelineBuilder<String, Prometheus.PrometheusValue> {
  private final String schemaId;
  private final String mongoUrl;

  public JsonPrometheusPushPipelineBuilder(final String schemaId, final String mongoUrl) {
    this.schemaId = schemaId;
    this.mongoUrl = mongoUrl;
  }

  @Override
  public DataStream<Prometheus.PrometheusValue> build(
      final StreamExecutionEnvironment executionEnvironment, final DataStreamSource<String> input)
      throws Exception {
    return input
        .process(new JsonSchemaStreamValidate(mongoUrl, schemaId))
        .process(new PrometheusFieldsConverter(mongoUrl, schemaId))
        .flatMap(
            new FlatMapFunction<
                Collection<Prometheus.PrometheusValue>, Prometheus.PrometheusValue>() {
              @Override
              public void flatMap(
                  final Collection<Prometheus.PrometheusValue> values,
                  final Collector<Prometheus.PrometheusValue> out)
                  throws Exception {
                for (final var value : values) {
                  out.collect(value);
                }
              }
            });
  }
}
