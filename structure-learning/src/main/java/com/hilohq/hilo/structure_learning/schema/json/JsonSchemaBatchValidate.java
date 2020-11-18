package com.hilohq.hilo.structure_learning.schema.json;

import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public final class JsonSchemaBatchValidate
    extends RichGroupReduceFunction<String, Fields<FieldKey>> {
  private final JsonSchemaValidate validate;

  public JsonSchemaBatchValidate(final String mongoUrl, final String schemaId) {
    this.validate = new JsonSchemaValidate(mongoUrl, schemaId);
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    validate.open(parameters);
  }

  @Override
  public void reduce(final Iterable<String> values, final Collector<Fields<FieldKey>> out)
      throws Exception {
    for (final var line : values) {
      validate.processElement(line, out);
    }
  }

  @Override
  public void close() throws Exception {
    validate.close();
  }
}
