package com.hilohq.hilo.integration_test.structure_learning;

import static com.hilohq.integration_tests.mongo.MongoUtils.insertResource;
import static com.hilohq.integration_tests.resources.ResourceUtils.copyResource;
import static com.hilohq.integration_tests.resources.ResourceUtils.readFromResource;
import static com.hilohq.integration_tests.services.ServiceUtils.createIntegrationsService;
import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.config.HiloConfig;
import com.hilohq.hilo.integrations_service.client.IntegrationsClient;
import com.hilohq.hilo.pipelines.*;
import com.hilohq.hilo.protobufs.integrations.Api;
import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class PrometheusMetricsGenerateTest {

  private static final HiloConfig CONFIG = HiloConfig.fromEnvironment();

  @Rule public GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private String projectId;
  private String schemaId;
  private String sourceId;
  private IntegrationsClient client;
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

    final var pair = createIntegrationsService(grpcCleanupRule);
    client = pair.client();
    pair.server().start();
  }

  private void generatePrometheusMetrics(final String filePath) throws Exception {
    final var executor = DefaultBatchExecutor.newBuilder().jarModule("integration-tests").build();

    executor.execute(
        PrometheusMetricsGeneratePipeline.newBuilder()
            .database(CONFIG.mongoDatabase())
            .mongoUrl(CONFIG.pipelineMongoUrl())
            .schemaId(schemaId)
            .inputPath(filePath)
            .build());
  }

  @Test
  public void testPrometheusMetricsGenerateBlocks() throws Exception {
    insertResource("samples/blocks/schema.json", "schemas", substitutions);
    insertResource("samples/blocks/features.json", "features", substitutions);
    copyResource("samples/blocks/logs.txt", "/tmp/blocks/logs.txt");

    final var expectedMetrics =
        readFromResource(
            "samples/blocks/prometheus.json",
            Prometheus.PrometheusMetricsModel.newBuilder().build(),
            substitutions);

    generatePrometheusMetrics("/tmp/blocks/logs.txt");

    final var response =
        client
            .getPrometheusMetrics(
                Api.GetPrometheusMetricsRequest.newBuilder().setSchemaId(schemaId).build())
            .get(5, TimeUnit.SECONDS);
    assertEquals(expectedMetrics.getMetricsMap(), response.getMetricsMap());
  }

  @Test
  public void testPrometheusMetricsGenerateTransactions() throws Exception {
    insertResource("samples/transactions/schema.json", "schemas", substitutions);
    insertResource(
        "samples/transactions/features.json",
        "features",
        FeatureOuterClass.FeaturesModel.newBuilder().build(),
        substitutions);
    copyResource("samples/transactions/logs.txt", "/tmp/transactions/logs.txt");

    final var expectedMetrics =
        readFromResource(
            "samples/transactions/prometheus.json",
            Prometheus.PrometheusMetricsModel.newBuilder().build(),
            substitutions);

    generatePrometheusMetrics("/tmp/transactions/logs.txt");

    final var response =
        client
            .getPrometheusMetrics(
                Api.GetPrometheusMetricsRequest.newBuilder().setSchemaId(schemaId).build())
            .get(5, TimeUnit.SECONDS);
    assertEquals(expectedMetrics.getMetricsMap(), response.getMetricsMap());
  }
}
