package com.hilohq.hilo.integration_test.structure_learning;

import static com.hilohq.integration_tests.resources.ResourceUtils.copyResource;
import static com.hilohq.integration_tests.resources.ResourceUtils.readStringResource;
import static com.hilohq.integration_tests.services.ServiceUtils.createFeaturesService;
import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.connectors.mongodb.client.MongoClients;
import com.hilohq.hilo.connectors.mongodb.flink.MongoOutputFormat;
import com.hilohq.hilo.features_service.client.FeaturesClient;
import com.hilohq.hilo.protobufs.structure.feature.Api;
import com.hilohq.hilo.structure_learning.features.mongo.FeaturesUpdateMongoCommandGenerator;
import com.hilohq.integration_tests.pipelines.JsonFeaturesGeneratePipelineBuilder;
import com.hilohq.integration_tests.pipelines.PipelineExecutor;
import io.grpc.testing.GrpcCleanupRule;
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

  @Before
  public void setUp() throws Exception {
    projectId = UUID.randomUUID().toString();
    schemaId = UUID.randomUUID().toString();
    sourceId = UUID.randomUUID().toString();

    final var pair = createFeaturesService(grpcCleanupRule);
    client = pair.client();
    pair.server().start();

    final var client = MongoClients.create("mongodb://root:example@localhost:27017/admin");
    final var schema =
        readStringResource("samples/schema.json")
            .replace("<schema_id>", schemaId)
            .replace("<source_id>", sourceId)
            .replace("<project_id>", projectId);
    final var document = Document.parse(schema);
    client.insertOne("test", "schemas", document).get(1, TimeUnit.SECONDS);
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
  public void testGenerateFeatureStatsFromJsonLogs() throws Exception {
    copyResource("samples/logs.txt", "/tmp/logs.txt");
    generateFeatures("/tmp/logs.txt");

    final var featuresResponse =
        client
            .getFeatures(Api.GetFeaturesRequest.newBuilder().setSchemaId(schemaId).build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(30, featuresResponse.getFeaturesCount());

    final var featureLogsResponse =
        client
            .getFeaturesLogs(Api.GetFeatureLogsRequest.newBuilder().setSchemaId(schemaId).build())
            .get(1, TimeUnit.SECONDS);
    assertEquals(30, featureLogsResponse.getLogsCount());
  }
}
