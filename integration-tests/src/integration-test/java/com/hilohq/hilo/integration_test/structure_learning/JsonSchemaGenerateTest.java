package com.hilohq.hilo.integration_test.structure_learning;

import static com.hilohq.integration_tests.resources.ResourceUtils.copyResource;
import static com.hilohq.integration_tests.services.ServiceUtils.createSchemaService;
import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.connectors.mongodb.flink.MongoOutputFormat;
import com.hilohq.hilo.protobufs.structure.Api;
import com.hilohq.hilo.schema_service.client.SchemaClient;
import com.hilohq.hilo.structure_learning.features.mongo.FeaturesUpdateMongoCommandGenerator;
import com.hilohq.hilo.structure_learning.schema.mongo.SchemaUpdateMongoCommandGenerator;
import com.hilohq.integration_tests.pipelines.JsonFeaturesGeneratePipelineBuilder;
import com.hilohq.integration_tests.pipelines.JsonSchemaGeneratePipelineBuilder;
import com.hilohq.integration_tests.pipelines.PipelineExecutor;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class JsonSchemaGenerateTest {

  @Rule public GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private String projectId;
  private String schemaId;
  private String sourceId;
  private SchemaClient client;

  @Before
  public void setUp() throws IOException {
    projectId = UUID.randomUUID().toString();
    schemaId = UUID.randomUUID().toString();
    sourceId = UUID.randomUUID().toString();

    final var pair = createSchemaService(grpcCleanupRule);
    client = pair.client();
    pair.server().start();
  }

  private void generateSchema(final String filePath) throws Exception {
    final var output =
        MongoOutputFormat.newBuilder(new SchemaUpdateMongoCommandGenerator())
            .database("test")
            .url("mongodb://root:example@mongodb:27017/admin")
            .build();

    final var executor = PipelineExecutor.newBuilder(output).jarModule("integration-tests").build();
    final var pipelineBuilder =
        new JsonSchemaGeneratePipelineBuilder(projectId, sourceId, schemaId);
    executor.execute(filePath, pipelineBuilder);
  }

  private void generateFeatures(final String filePath) throws Exception {
    final var output =
        MongoOutputFormat.newBuilder(new FeaturesUpdateMongoCommandGenerator())
            .database("test")
            .url("mongodb://root:example@mongodb:27017/admin")
            .build();

    final var schemaId = "6a733a83-5f81-413e-90bb-0f809f242f52";
    final var executor = PipelineExecutor.newBuilder(output).jarModule("integration-tests").build();
    final var pipelineBuilder =
        new JsonFeaturesGeneratePipelineBuilder(
            schemaId, "mongodb://root:example@mongodb:27017/admin");
    executor.execute(filePath, pipelineBuilder);
  }

  @Test
  public void testGenerateSchemaFromJsonLogs() throws Exception {
    copyResource("samples/logs.txt", "/tmp/logs.txt");
    generateSchema("/tmp/logs.txt");

    final var schemaIdsResponse =
        client
            .getSchemaIdsBySource(
                Api.GetSchemaIdsBySourceRequest.newBuilder().setSourceId(sourceId).build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(1, schemaIdsResponse.getSchemaIdCount());

    final var schemaResponse =
        client
            .getSchema(
                Api.GetSchemaRequest.newBuilder()
                    .setSchemaId(schemaIdsResponse.getSchemaId(0))
                    .build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(32, schemaResponse.getJson().getFieldsCount());

    final var logsResponse =
        client
            .getSchemaLogs(
                Api.GetSchemaLogsRequest.newBuilder()
                    .setSchemaId(schemaIdsResponse.getSchemaId(0))
                    .build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(32, logsResponse.getJson().getLogsCount());
  }

  @Test
  public void testGenerateFeatureStatsFromJsonLogs() throws Exception {
    copyResource("samples/logs.txt", "/tmp/logs.txt");
    generateFeatures("/tmp/logs.txt");
  }
}
