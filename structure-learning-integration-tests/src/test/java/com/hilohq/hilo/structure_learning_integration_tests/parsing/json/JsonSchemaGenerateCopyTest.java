package com.hilohq.hilo.structure_learning_integration_tests.parsing.json;

import com.hilohq.hilo.hilo_flink.mongodb.MongoDbSink;
import com.hilohq.hilo.protobufs.parsing.Schema;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaFieldsAggregator;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaGenerate;
import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.JsonSerializationSchema;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.ProtobufDeserializationSchema;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.ProtobufSerializationSchema;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.TextLineBatchSource;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
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

    final var props = new Properties();
    props.put("bootstrap.servers", "kafka:9092");

    final var consumer =
        new FlinkKafkaConsumer<>(
            "SchemaEvent",
            new ProtobufDeserializationSchema<>(Schema.SchemaEvent.newBuilder().build()),
            props).setStartFromEarliest();
    final var events = executionEnvironment.addSource(consumer);
    final var updates = events.process(new JsonSchemaFieldsAggregator());
    final var sink =
        new MongoDbSink<Schema.SchemaUpdate>(
            "mongodb://root:example@mongodb:27017/admin", new JsonSerializationSchema<>(), "test", "example");
    updates.addSink(sink);

    executionEnvironment.execute("UpdateLoading");
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
