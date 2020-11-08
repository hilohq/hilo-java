package com.hilohq.hilo.structure_learning.parsing.fields;

import com.hilohq.hilo.structure_learning.parsing.schema.Samples;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public final class FieldStatsGenerate
    extends KeyedProcessFunction<FieldStats, Samples, FieldStats> {

  private ValueState<FieldsStats> state;

  @Override
  public void open(final Configuration parameters) throws Exception {
    state = getRuntimeContext().getState(new ValueStateDescriptor<>("stats", FieldsStats.class));
    final var stats = state.value();
    if (stats == null) {
      state.update(new FieldsStats());
    }
  }

  @Override
  public void processElement(
      final Samples value, final Context ctx, final Collector<FieldStats> out) throws Exception {
    final var stats = state.value();
  }
}
