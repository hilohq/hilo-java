package com.hilohq.hilo.structure_learning_integration_tests.parsing.json;

import com.hilohq.hilo.connector.mongodb.flink.MongoSink;
import com.hilohq.hilo.protobufs.schema.Model;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaFieldsAggregator;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaGenerate;
import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.*;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.junit.Test;

public final class JsonSchemaGenerateTest {

  private void generateSchema(final List<TextLineBatch> batches) throws Exception {
    final var executionEnvironment =
        StreamExecutionEnvironment.createRemoteEnvironment(
            "127.0.0.1",
            8081,
            "/home/eauge/Workspace/github.com/hilohq/hilo-java/structure-learning-integration-tests/build/libs/structure-learning-integration-tests-0.0.1-all.jar");

    final var records =
        executionEnvironment.addSource(new TextLineBatchSource(batches)).name("source");
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
        records.process(new JsonSchemaGenerate()).name("generate-schema");

    events.addSink(producer).name("sink");
    executionEnvironment.execute("EventGeneration");
  }

  private void storeSchema() throws Exception {
    final StreamExecutionEnvironment executionEnvironment =
        StreamExecutionEnvironment.createRemoteEnvironment(
            "127.0.0.1",
            8081,
            "/home/eauge/Workspace/github.com/hilohq/hilo-java/structure-learning-integration-tests/build/libs/structure-learning-integration-tests-0.0.1-all.jar");
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

    final var client = executionEnvironment.executeAsync("UpdateLoading");
    Thread.sleep(10000);
    client.cancel().get(10, TimeUnit.SECONDS);
  }

  @Test
  public void testGenerateSchemaOneBatchCreateEvents() throws Exception {
    generateSchema(
        Collections.singletonList(
            TextLineBatch.fromLines(
                "{\"integer\": 1, \"string\": \"hello\"}",
                "{\"integer\": 2, \"string\": \"bye\"}")));
    storeSchema();
  }
}
