package com.hilohq.hilo.structure_learning.schema.json;

import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public final class JsonSchemaStreamValidate extends ProcessFunction<String, Fields<FieldKey>> {
  private final JsonSchemaValidate validate;

  public JsonSchemaStreamValidate(final String mongoUrl, final String schemaId) {
    this.validate = new JsonSchemaValidate(mongoUrl, schemaId);
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    validate.open(parameters);
  }

  @Override
  public void processElement(
      final String value, final Context ctx, final Collector<Fields<FieldKey>> out)
      throws Exception {
    validate.processElement(value, out);
  }

  @Override
  public void close() throws Exception {
    validate.close();
  }
}
