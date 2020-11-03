package com.hilohq.hilo.structure_learning.parsing.events;

import com.hilohq.hilo.protobufs.parsing.Schema;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public final class SchemaFieldsAggregator
    extends ProcessFunction<Schema.SchemaEvent, Schema.SchemaUpdate> {

  private final Schema.SchemaFields.Builder fields;

  public SchemaFieldsAggregator() {
    this.fields = Schema.SchemaFields.newBuilder();
  }

  @Override
  public void processElement(
      final Schema.SchemaEvent event, final Context ctx, final Collector<Schema.SchemaUpdate> out)
      throws Exception {
    if (!event.getData().hasFieldStats()) {
      return;
    }

    final var stats = event.getData().getFieldStats();
    if (stats.getFieldStats().hasJson()) {}
  }
}
