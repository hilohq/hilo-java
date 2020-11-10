package com.hilohq.hilo.integration_tests.structure_learning;

import com.hilohq.hilo.structure_learning.schema.log.LogSchemaStatsGenerate;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.junit.Test;

public final class SchemaGenerateTest {

    public void generateSchema() {
        final var executionEnvironment =
                ExecutionEnvironment.createRemoteEnvironment(
                        "127.0.0.1",
                        8081,
                        "/home/eaugeas/Workspace/github.com/hilohq/hilo-java/structure-learning-integration-tests/build/libs/structure-learning-integration-tests-0.0.1-all.jar");

        final var lines = executionEnvironment.readTextFile("/tmp/logs.txt");
        final var stats = lines.reduceGroup(new LogSchemaStatsGenerate());

    }

    @Test
    public void testExample() {

    }
}
