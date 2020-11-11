package com.hilohq.hilo.integration_tests.structure_learning;

import static com.hilohq.integration_tests.resources.ResourceUtils.copyResource;

import com.hilohq.hilo.connectors.mongodb.flink.MongoOutputFormat;
import com.hilohq.hilo.structure_learning.schema.base.SchemaUpdateMongoCommandGenerator;
import com.hilohq.hilo.structure_learning.schema.json.JsonFieldStatsKeySelector;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaStatsAggregate;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaStatsGenerate;
import com.hilohq.integration_tests.jars.JarUtils;
import java.util.UUID;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.junit.Test;

public final class JsonSchemaGenerateTest {

  private static final String PROJECT_ID = UUID.randomUUID().toString();
  private static final String SCHEMA_ID = UUID.randomUUID().toString();
  private static final String SOURCE_ID = UUID.randomUUID().toString();

  public void generateSchemaStats(final String filePath) throws Exception {
    final var jarPath = JarUtils.jarPathForModule("integration-tests");
    final var executionEnvironment =
        ExecutionEnvironment.createRemoteEnvironment("127.0.0.1", 8081, jarPath.toString());

    final var lines = executionEnvironment.readTextFile(filePath);
    final var output =
        MongoOutputFormat.newBuilder(new SchemaUpdateMongoCommandGenerator())
            .database("test")
            .url("mongodb://root:example@mongodb:27017/admin")
            .build();
    lines
        .reduceGroup(new JsonSchemaStatsGenerate(PROJECT_ID, SOURCE_ID, SCHEMA_ID))
        .groupBy(new JsonFieldStatsKeySelector())
        .reduceGroup(new JsonSchemaStatsAggregate())
        .output(output);

    executionEnvironment.execute();
  }

  @Test
  public void testExample() throws Exception {
    copyResource("samples/logs.txt", "/tmp/logs.txt");
    generateSchemaStats("/tmp/logs.txt");
  }
}
