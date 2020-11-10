package com.hilohq.hilo.structure_learning_integration_tests.parsing.json;

import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.connector.mongodb.flink.MongoSink;
import com.hilohq.hilo.protobufs.schema.Api;
import com.hilohq.hilo.protobufs.schema.Model;
import com.hilohq.hilo.schema_service.client.SchemaClient;
import com.hilohq.hilo.schema_service.service.MongoSchemaStore;
import com.hilohq.hilo.schema_service.service.SchemaService;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaFieldsAggregator;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaGenerate;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.*;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.junit.Rule;
import org.junit.Test;

public final class JsonSchemaGenerateTest {

  @Rule public GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private static final String PROJECT_ID = "PROJECT_ID" + UUID.randomUUID().toString();
  private static final String SOURCE_ID = "SOURCE_ID" + UUID.randomUUID().toString();
  private static final String SCHEMA_ID = "SCHEMA_ID";

  private void generateSchema() throws Exception {
    final var executionEnvironment =
        StreamExecutionEnvironment.createRemoteEnvironment(
            "127.0.0.1",
            8081,
            "/home/eaugeas/Workspace/github.com/hilohq/hilo-java/structure-learning-integration-tests/build/libs/structure-learning-integration-tests-0.0.1-all.jar");

    final var records =
        executionEnvironment.addSource(new FileLineSource("/tmp/logs.txt")).name("source");

    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionEnvironment
        .getConfig()
        .addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);
    final Class<?> type = Class.forName("com.google.protobuf.MapEntry");
    executionEnvironment.getConfig().registerTypeWithKryoSerializer(type, ProtobufSerializer.class);

    final var producer =
        new FlinkKafkaProducer<Model.SchemaEvent>(
            "kafka:9092", "SchemaEvent", new ProtobufSerializationSchema<>());
    final DataStream<Model.SchemaEvent> events =
        records
            .process(new JsonSchemaGenerate(PROJECT_ID, SOURCE_ID, SCHEMA_ID))
            .name("generate-schema");

    events.addSink(producer).name("sink");
    executionEnvironment.execute("EventGeneration");
  }

  private JobClient storeSchema() throws Exception {
    final StreamExecutionEnvironment executionEnvironment =
        StreamExecutionEnvironment.createRemoteEnvironment(
            "127.0.0.1",
            8081,
            "/home/eaugeas/Workspace/github.com/hilohq/hilo-java/structure-learning-integration-tests/build/libs/structure-learning-integration-tests-0.0.1-all.jar");
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionEnvironment
        .getConfig()
        .addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);

    final var props = new Properties();
    props.put("bootstrap.servers", "kafka:9092");

    final var consumer =
        new FlinkKafkaConsumer<>(
                "SchemaEvent",
                new ProtobufDeserializationSchema<>(Model.SchemaEvent.newBuilder().build()),
                props)
            .setStartFromEarliest();
    final var events = executionEnvironment.addSource(consumer);
    final var updates = events.process(new JsonSchemaFieldsAggregator());
    final var sink =
        new MongoSink<>(
            "mongodb://root:example@mongodb:27017/admin",
            "test",
            new JsonSchemaUpdateCommandGenerator());
    updates.addSink(sink);

    return executionEnvironment.executeAsync("UpdateLoading");
  }

  @Test
  public void testGenerateSchemaOneBatchCreateEvents() throws Exception {
    generateSchema();
    final var jobClient = storeSchema();

    final var store = MongoSchemaStore.open("mongodb://root:example@localhost:27017/admin");
    final var service = new SchemaService(store);
    final var channel = InProcessChannelBuilder.forName("schema").directExecutor().build();
    final var client = new SchemaClient(channel);
    final var server =
        InProcessServerBuilder.forName("schema").directExecutor().addService(service).build();
    grpcCleanupRule.register(server);
    grpcCleanupRule.register(channel);
    server.start();

    for (int i = 0; i < 10; i++) {
      final var result =
          client
              .getSchema(Api.GetSchemaRequest.newBuilder().setSourceId(SOURCE_ID).build())
              .get(1, TimeUnit.SECONDS);
      if (result.getSchemasCount() < 1) {
        Thread.sleep(1000);
        continue;
      }

      assertEquals(result.getSchemasCount(), 1);
      assertEquals(PROJECT_ID, result.getSchemas(0).getProjectId());
      assertEquals(SOURCE_ID, result.getSchemas(0).getSourceId());
      break;
    }

    jobClient.cancel().get(10, TimeUnit.SECONDS);
  }
}
