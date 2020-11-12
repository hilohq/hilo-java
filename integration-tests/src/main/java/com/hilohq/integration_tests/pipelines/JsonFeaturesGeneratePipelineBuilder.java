package com.hilohq.integration_tests.pipelines;

import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.structure_learning.schema.json.JsonSchemaValidate;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.DataSource;

public final class JsonFeaturesGeneratePipelineBuilder
    implements PipelineBuilder<String, FieldsMap<FieldKey>> {
  private final String schemaId;
  private final String mongoUrl;

  public JsonFeaturesGeneratePipelineBuilder(final String schemaId, final String mongoUrl) {
    this.schemaId = schemaId;
    this.mongoUrl = mongoUrl;
  }

  @Override
  public DataSet<FieldsMap<FieldKey>> build(final DataSource<String> input) {
    return input.reduceGroup(new JsonSchemaValidate(mongoUrl, schemaId));
  }
}
