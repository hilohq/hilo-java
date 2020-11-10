package com.hilohq.hilo.structure_learning_integration_tests.parsing.json;

import com.hilohq.hilo.protobuf.stats.Stats;
import com.hilohq.hilo.structure_learning.parsing.fields.FieldStatsGenerate;
import com.hilohq.hilo.structure_learning.parsing.json.JsonSchemaValidate;
import com.hilohq.hilo.structure_learning.parsing.schema.Samples;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.FileLineSource;
import com.hilohq.hilo.structure_learning_integration_tests.parsing.helpers.ProtobufSerializationSchema;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.junit.Test;

public final class JsonStatsGenerateTest {

  private static final String SCHEMA_ID = "SCHEMA_ID";

  private void generateStats() throws Exception {
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

    final DataStream<Samples> samples =
        records
            .process(
                new JsonSchemaValidate(
                    "test", "mongodb://root:example@mongodb:27017/admin", SCHEMA_ID))
            .name("schema-validate");

    final DataStream<Stats.StatsEvent> events = samples.process(new FieldStatsGenerate());
    final var producer =
        new FlinkKafkaProducer<Stats.StatsEvent>(
            "kafka:9092", "StatsEvent", new ProtobufSerializationSchema<>());

    events.addSink(producer).name("sink");
    executionEnvironment.execute("EventGeneration");
  }

  @Test
  public void testGenerateStatsOneBatch() throws Exception {
    generateStats();
  }
}
