package com.hilohq.hilo.integration_tests.structure_learning;

import static com.hilohq.integration_tests.resources.ResourceUtils.copyResource;

import com.hilohq.hilo.connectors.mongodb.flink.MongoOutputFormat;
import com.hilohq.hilo.protobufs.structure.Api;
import com.hilohq.hilo.schema_service.client.SchemaClient;
import com.hilohq.hilo.schema_service.service.MongoSchemaStore;
import com.hilohq.hilo.schema_service.service.SchemaService;
import com.hilohq.hilo.structure_learning.schema.base.SchemaUpdateMongoCommandGenerator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.hilohq.integration_tests.pipelines.JsonSchemaGeneratePipelineBuilder;
import com.hilohq.integration_tests.pipelines.PipelineExecutor;
import com.hilohq.integration_tests.resources.ResourceUtils;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Test;

public final class JsonSchemaGenerateTest {

  @Rule
  public GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private static final String PROJECT_ID = UUID.randomUUID().toString();
  private static final String SCHEMA_ID = UUID.randomUUID().toString();
  private static final String SOURCE_ID = UUID.randomUUID().toString();

  public void generateSchemaStats(final String filePath) throws Exception {
    final var output =
            MongoOutputFormat.newBuilder(new SchemaUpdateMongoCommandGenerator())
                    .database("test")
                    .url("mongodb://root:example@mongodb:27017/admin")
                    .build();

    final var executor = PipelineExecutor.newBuilder(output).jarModule("integration-tests").build();
    ResourceUtils.copyResource("samples/logs.txt", "/tmp/logs.txt");
    final var pipelineBuilder = new JsonSchemaGeneratePipelineBuilder(
            PROJECT_ID, SCHEMA_ID, SOURCE_ID);
    executor.execute("/tmp/logs.txt", pipelineBuilder);
  }

  @Test
  public void testExample() throws Exception {
    final var store = MongoSchemaStore.open("mongodb://root:example@localhost:27017/admin");
    final var service = new SchemaService(store);
    final var channel = InProcessChannelBuilder.forName("schema").directExecutor().build();
    final var client = new SchemaClient(channel);
    final var server =
            InProcessServerBuilder.forName("schema").directExecutor().addService(service).build();
    grpcCleanupRule.register(server);
    grpcCleanupRule.register(channel);
    server.start();

//    copyResource("samples/logs.txt", "/tmp/logs.txt");
//    generateSchemaStats("/tmp/logs.txt");
    final String sourceId = "06261c78-f892-4a6b-a2e3-3381df003b0c";
    final var schemaIdsResponse = client
            .getSchemaIdsBySource(Api.GetSchemaIdsBySourceRequest.newBuilder().setSourceId(sourceId).build())
            .get(1, TimeUnit.SECONDS);
    System.out.println("RESPONSE " + schemaIdsResponse);

    final var schemaResponse = client.getSchema(Api.GetSchemaRequest.newBuilder().setSchemaId(schemaIdsResponse.getSchemaId(0)).build()).get(1, TimeUnit.SECONDS);
    System.out.println("RESPONSE " + schemaResponse);
  }
}
