package com.hilohq.hilo.integration_test.structure_learning;

import static com.hilohq.integration_tests.resources.ResourceUtils.*;
import static com.hilohq.integration_tests.services.ServiceUtils.createFeaturesService;
import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.connectors.mongodb.client.MongoClients;
import com.hilohq.hilo.connectors.mongodb.flink.MongoOutputFormat;
import com.hilohq.hilo.features_service.client.FeaturesClient;
import com.hilohq.hilo.protobufs.structure.feature.Api;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.hilohq.hilo.structure_learning.features.mongo.FeaturesUpdateMongoCommandGenerator;
import com.hilohq.integration_tests.pipelines.JsonFeaturesGeneratePipelineBuilder;
import com.hilohq.integration_tests.pipelines.PipelineExecutor;
import io.grpc.testing.GrpcCleanupRule;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class FeaturesGenerateTest {

  @Rule public GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private String projectId;
  private String schemaId;
  private String sourceId;
  private FeaturesClient client;
  private Map<String, String> substitutions;

  @Before
  public void setUp() throws Exception {
    projectId = UUID.randomUUID().toString();
    schemaId = UUID.randomUUID().toString();
    sourceId = UUID.randomUUID().toString();

    substitutions = new HashMap<>();
    substitutions.put("$PROJECT_ID", projectId);
    substitutions.put("$SOURCE_ID", sourceId);
    substitutions.put("$SCHEMA_ID", schemaId);

    final var pair = createFeaturesService(grpcCleanupRule);
    client = pair.client();
    pair.server().start();
  }

  private void insertSchema(final String schemaPath) throws Exception {
    final var client = MongoClients.create("mongodb://root:example@localhost:27017/admin");
    final var schema =
        readStringResource(schemaPath)
            .replace("$SCHEMA_ID", schemaId)
            .replace("$SOURCE_ID", sourceId)
            .replace("$PROJECT_ID", projectId);
    final var document = Document.parse(schema);
    client.insertOne("test", "schemas", document).get(1, TimeUnit.SECONDS);
    client.close();
  }

  private void generateFeatures(final String filePath) throws Exception {
    final var output =
        MongoOutputFormat.newBuilder(new FeaturesUpdateMongoCommandGenerator())
            .database("test")
            .url("mongodb://root:example@mongodb:27017/admin")
            .build();

    final var executor = PipelineExecutor.newBuilder(output).jarModule("integration-tests").build();
    final var pipelineBuilder =
        new JsonFeaturesGeneratePipelineBuilder(
            schemaId, "mongodb://root:example@mongodb:27017/admin");
    executor.execute(filePath, pipelineBuilder);
  }

  @Test
  public void testGenerateFeatureStatsForBlocks() throws Exception {
    insertSchema("samples/blocks/schema.json");
    copyResource("samples/blocks/logs.txt", "/tmp/blocks/logs.txt");
    generateFeatures("/tmp/blocks/logs.txt");
    final var expectedFeatures =
        readFromResource(
            "samples/blocks/features.json",
            FeatureOuterClass.FeaturesModel.newBuilder().build(),
            substitutions);

    final var featuresResponse =
        client
            .getFeatures(Api.GetFeaturesRequest.newBuilder().setSchemaId(schemaId).build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(expectedFeatures.getFeaturesMap(), featuresResponse.getFeaturesMap());

    final var featureLogsResponse =
        client
            .getFeaturesLogs(Api.GetFeatureLogsRequest.newBuilder().setSchemaId(schemaId).build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(18, featureLogsResponse.getLogsCount());
  }

  @Test
  public void testGenerateFeatureStatsForTransactions() throws Exception {
    insertSchema("samples/transactions/schema.json");
    copyResource("samples/transactions/logs.txt", "/tmp/transactions/logs.txt");
    generateFeatures("/tmp/transactions/logs.txt");
    final var expectedFeatures =
        readFromResource(
            "samples/transactions/features.json",
            FeatureOuterClass.FeaturesModel.newBuilder().build(),
            substitutions);

    final var featuresResponse =
        client
            .getFeatures(Api.GetFeaturesRequest.newBuilder().setSchemaId(schemaId).build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(expectedFeatures.getFeaturesMap(), featuresResponse.getFeaturesMap());

    final var featureLogsResponse =
        client
            .getFeaturesLogs(Api.GetFeatureLogsRequest.newBuilder().setSchemaId(schemaId).build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(14, featureLogsResponse.getLogsCount());
  }
}
