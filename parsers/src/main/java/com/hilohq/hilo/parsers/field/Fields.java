package com.hilohq.hilo.parsers.field;

import com.hilohq.hilo.protobufs.structure.schema.Schema;

public final class Fields<K extends FieldKey> {

  private final Schema.SchemaMetadata metadata;
  private final FieldsMap<K> map;

  public Fields(final Schema.SchemaMetadata metadata, final FieldsMap<K> map) {
    this.metadata = metadata;
    this.map = map;
  }

  public Schema.SchemaMetadata metadata() {
    return metadata;
  }

  public FieldsMap<K> map() {
    return map;
  }
}
