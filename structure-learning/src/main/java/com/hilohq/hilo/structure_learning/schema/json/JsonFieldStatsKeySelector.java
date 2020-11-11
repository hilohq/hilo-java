package com.hilohq.hilo.structure_learning.schema.json;

import com.hilohq.hilo.protobufs.structure.Schema;
import org.apache.flink.api.java.functions.KeySelector;

public final class JsonFieldStatsKeySelector implements KeySelector<Schema.SchemaEvent, String> {
  @Override
  public String getKey(final Schema.SchemaEvent value) throws Exception {
    if (!value.hasJson() || !value.getJson().hasFieldStats()) {
      throw new IllegalArgumentException("JsonFieldStatsKeySelector only expects FieldStats");
    }
    return value.getJson().getFieldStats().getKey();
  }
}
