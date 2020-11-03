package com.hilohq.hilo.structure_learning_integration_tests.parsing.json;

import com.google.protobuf.Message;
import com.hilohq.hilo.hilo_flink.mongodb.MongoDbSink;
import com.hilohq.hilo.protobufs.schema.Json;
import com.hilohq.hilo.protobufs.schema.Schema;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaFieldsAggregator;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaGenerate;
import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.JsonSerializationSchema;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.ProtobufDeserializationSchema;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.ProtobufSerializationSchema;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.TextLineBatchSource;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.junit.Test;

public final class JsonSchemaGenerateCopyTest {

  private void generateSchema(final List<TextLineBatch> batches) throws Exception {
    final var executionEnvironment =
        StreamExecutionEnvironment.createRemoteEnvironment(
            "127.0.0.1",
            8081,
            "/home/eaugeas/Workspace/github.com/hilohq/hilo-java/structure-learning-integration-tests/build/libs/structure-learning-integration-tests-0.0.1-all.jar");

    final var records =
        executionEnvironment.addSource(new TextLineBatchSource(batches)).name("source");
    final Class<?> unmodColl = Class.forName("java.util.Collections$UnmodifiableCollection");
    executionEnvironment
            .getConfig()
            .addDefaultKryoSerializer(unmodColl, UnmodifiableCollectionsSerializer.class);

    final var producer =
        new FlinkKafkaProducer<Schema.SchemaEvent>(
            "kafka:9092", "SchemaEvent", new ProtobufSerializationSchema<>());
    final DataStream<Schema.SchemaEvent> events =
        records.process(new JsonSchemaGenerate()).name("generate-schema");

    events.addSink(producer).name("sink");
    executionEnvironment.execute("EventGeneration");
  }

  private void storeSchema() throws Exception {
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
                new ProtobufDeserializationSchema<>(Schema.SchemaEvent.newBuilder().build()),
                props)
            .setStartFromEarliest();
    final var events = executionEnvironment.addSource(consumer);
    final var updates = events.process(new JsonSchemaFieldsAggregator());
    final var sink =
        new MongoDbSink<Schema.SchemaUpdate>(
            "mongodb://root:example@mongodb:27017/admin",
            new JsonSerializationSchema<>(),
            "test",
            "example");
    updates.addSink(sink);

    final var client = executionEnvironment.executeAsync("UpdateLoading");
    Thread.sleep(10000);
    client.cancel().get(10, TimeUnit.SECONDS);
  }

  @Test
  public void testGenerateSchemaOneBatchCreateEvents() throws Exception {
//    generateSchema(
//        Collections.singletonList(
//            TextLineBatch.fromLines(
//                "{\"integer\": 1, \"string\": \"hello\"}",
//                "{\"integer\": 2, \"string\": \"bye\"}")));
    storeSchema();
  }
}
