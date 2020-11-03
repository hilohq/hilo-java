package com.hilohq.hilo.structure_learning.parsing.json;

import com.hilohq.hilo.structure_learning.parsing.schema.Batch;
import com.hilohq.hilo.structure_learning.parsing.schema.SchemaValidate;
import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import org.apache.flink.util.Collector;

public final class JsonSchemaValidate extends SchemaValidate<TextLineBatch> {

  public JsonSchemaValidate() {}

  @Override
  public void processElement(
      final TextLineBatch value, final Context ctx, final Collector<Batch> out) throws Exception {}
}
