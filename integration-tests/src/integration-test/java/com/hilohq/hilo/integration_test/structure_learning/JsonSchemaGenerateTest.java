package com.hilohq.hilo.integration_test.structure_learning;

import static com.hilohq.integration_tests.resources.ResourceUtils.copyResource;
import static com.hilohq.integration_tests.resources.ResourceUtils.readFromResource;
import static com.hilohq.integration_tests.services.ServiceUtils.createSchemaService;
import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.pipelines.DefaultBatchExecutor;
import com.hilohq.hilo.pipelines.JsonSchemaGeneratePipeline;
import com.hilohq.hilo.protobufs.structure.schema.Api;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.hilohq.hilo.schema_service.client.SchemaClient;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class JsonSchemaGenerateTest {

  private static final HiloConfig CONFIG = HiloConfig.fromEnvironment();

  @Rule public GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private String projectId;
  private String schemaId;
  private String sourceId;
  private SchemaClient client;
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

    final var pair = createSchemaService(grpcCleanupRule);
    client = pair.client();
    pair.server().start();
  }

  private void generateSchema(final String filePath) throws Exception {
    final var executor = DefaultBatchExecutor.newBuilder().jarModule("integration-tests").build();

    executor.execute(
        JsonSchemaGeneratePipeline.newBuilder()
            .database(CONFIG.mongoDatabase())
            .mongoUrl(CONFIG.pipelineMongoUrl())
            .projectId(projectId)
            .schemaId(schemaId)
            .sourceId(sourceId)
            .inputPath(filePath)
            .build());
  }

  @Test
  public void testGenerateSchemaForBlocks() throws Exception {
    copyResource("samples/blocks/logs.txt", "/tmp/blocks/logs.txt");
    generateSchema("/tmp/blocks/logs.txt");
    final var expectedSchema =
        readFromResource(
            "samples/blocks/schema.json",
            Schema.SchemaFieldsModel.newBuilder().build(),
            substitutions);
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
    assertEquals(expectedSchema.getJson(), schemaResponse.getJson());

    final var logsResponse =
        client
            .getSchemaLogs(
                Api.GetSchemaLogsRequest.newBuilder()
                    .setSchemaId(schemaIdsResponse.getSchemaId(0))
                    .build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(20, logsResponse.getJson().getLogsCount());
  }

  @Test
  public void testGenerateSchemaForTransactions() throws Exception {
    copyResource("samples/transactions/logs.txt", "/tmp/transactions/logs.txt");
    generateSchema("/tmp/transactions/logs.txt");
    final var expectedSchema =
        readFromResource(
            "samples/transactions/schema.json",
            Schema.SchemaFieldsModel.newBuilder().build(),
            substitutions);

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
    assertEquals(expectedSchema.getJson(), schemaResponse.getJson());
    ;

    final var logsResponse =
        client
            .getSchemaLogs(
                Api.GetSchemaLogsRequest.newBuilder()
                    .setSchemaId(schemaIdsResponse.getSchemaId(0))
                    .build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(14, logsResponse.getJson().getLogsCount());
  }
}
