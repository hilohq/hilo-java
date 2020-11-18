package com.hilohq.hilo.integration_test.structure_learning;

import static com.hilohq.integration_tests.mongo.MongoUtils.insertResource;
import static com.hilohq.integration_tests.resources.ResourceUtils.copyResource;

import com.hilohq.hilo.protobufs.connectors.prometheus.Prometheus;
import com.hilohq.integration_tests.pipelines.JsonPrometheusPushPipelineBuilder;
import com.hilohq.integration_tests.pipelines.StreamPipelineExecutor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.flink.api.java.io.TextOutputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.functions.sink.OutputFormatSinkFunction;
import org.junit.Before;
import org.junit.Test;

public final class PrometheusPushTest {

  private String projectId;
  private String schemaId;
  private String sourceId;
  private Map<String, String> substitutions;

  @Before
  public void setUp() throws IOException {
    projectId = UUID.randomUUID().toString();
    schemaId = UUID.randomUUID().toString();
    sourceId = UUID.randomUUID().toString();

    substitutions = new HashMap<>();
    substitutions.put("$PROJECT_ID", projectId);
    substitutions.put("$SOURCE_ID", sourceId);
    substitutions.put("$SCHEMA_ID", schemaId);
  }

  private void pushToPrometheus(final String filePath) throws Exception {
    final var path = new Path("/tmp/results.txt");
    final var sink =
        new OutputFormatSinkFunction<>(
            new TextOutputFormat<Prometheus.PrometheusValue>(new Path("/tmp/results.txt")));
    final var executor =
        StreamPipelineExecutor.newBuilder(sink).jarModule("integration-tests").build();
    final var pipelineBuilder =
        new JsonPrometheusPushPipelineBuilder(
            schemaId, "mongodb://root:example@mongodb:27017/admin");
    executor.execute(filePath, pipelineBuilder);
  }

  @Test
  public void testPrometheusPushBlocks() throws Exception {
    insertResource("samples/blocks/schema.json", "schemas", substitutions);
    insertResource("samples/blocks/features.json", "features", substitutions);
    copyResource("samples/blocks/logs.txt", "/tmp/blocks/logs.txt");

    pushToPrometheus("/tmp/blocks/logs.txt");
  }
}
