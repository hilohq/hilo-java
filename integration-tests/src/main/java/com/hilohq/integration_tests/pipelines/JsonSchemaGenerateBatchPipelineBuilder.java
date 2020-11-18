package com.hilohq.integration_tests.pipelines;

import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.hilohq.hilo.structure_learning.schema.json.JsonFieldStatsKeySelector;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaStatsGenerate;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaUpdateGenerate;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;

public final class JsonSchemaGenerateBatchPipelineBuilder
    implements BatchPipelineBuilder<String, Schema.SchemaUpdate> {

  private final String projectId;
  private final String sourceId;
  private final String schemaId;

  public JsonSchemaGenerateBatchPipelineBuilder(
      final String projectId, final String sourceId, final String schemaId) {
    this.projectId = projectId;
    this.sourceId = sourceId;
    this.schemaId = schemaId;
  }

  @Override
  public DataSet<Schema.SchemaUpdate> build(
      final ExecutionEnvironment executionEnvironment, final DataSource<String> input) {
    return input
        .reduceGroup(new JsonSchemaStatsGenerate(projectId, sourceId, schemaId))
        .groupBy(new JsonFieldStatsKeySelector())
        .reduceGroup(new JsonSchemaUpdateGenerate());
  }
}
