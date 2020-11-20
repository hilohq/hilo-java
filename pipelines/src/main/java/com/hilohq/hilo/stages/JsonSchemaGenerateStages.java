package com.hilohq.hilo.stages;

import static com.google.common.base.Verify.verifyNotNull;

import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.hilohq.hilo.structure_learning.schema.json.JsonFieldStatsKeySelector;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaStatsGenerate;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaUpdateGenerate;
import java.util.UUID;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.DataSet;

public final class JsonSchemaGenerateStages implements BatchStages<String, Schema.SchemaUpdate> {
  private final String projectId;
  private final String sourceId;
  private final String schemaId;

  public static final class Builder {
    private String projectId = UUID.randomUUID().toString();
    private String sourceId = UUID.randomUUID().toString();
    private String schemaId = UUID.randomUUID().toString();

    public Builder projectId(final String projectId) {
      verifyNotNull(projectId);
      this.projectId = projectId;
      return this;
    }

    public Builder sourceId(final String sourceId) {
      verifyNotNull(sourceId);
      this.sourceId = sourceId;
      return this;
    }

    public Builder schemaId(final String schemaId) {
      verifyNotNull(schemaId);
      this.schemaId = schemaId;
      return this;
    }

    public JsonSchemaGenerateStages build() {
      return new JsonSchemaGenerateStages(projectId, sourceId, schemaId);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  JsonSchemaGenerateStages(final String projectId, final String sourceId, final String schemaId) {
    this.projectId = projectId;
    this.sourceId = sourceId;
    this.schemaId = schemaId;
  }

  @Override
  public DataSet<Schema.SchemaUpdate> bind(
      final ExecutionConfig config, final DataSet<String> input) throws Exception {
    return input
        .reduceGroup(new JsonSchemaStatsGenerate(projectId, sourceId, schemaId))
        .groupBy(new JsonFieldStatsKeySelector())
        .reduceGroup(new JsonSchemaUpdateGenerate());
  }
}
