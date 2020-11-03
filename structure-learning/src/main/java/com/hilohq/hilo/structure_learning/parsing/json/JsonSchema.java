package com.hilohq.hilo.structure_learning.parsing.json;

import static com.hilohq.hilo.structure_learning.parsing.json.entity.JsonUtils.fieldFullName;

import com.hilohq.hilo.protobufs.parsing.Json;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class JsonSchema implements Serializable {
  private final Map<String, Json.JsonField> fields;

  public JsonSchema() {
    fields = new HashMap<>();
  }

  public Collection<Json.JsonField> fields() {
    return fields.values();
  }

  public void put(final String path, final String name, final Json.JsonType type) {
    put(Json.JsonField.newBuilder().setPath(path).setName(name).setType(type).build());
  }

  public void put(final Json.JsonField field) {
    final String key = fieldFullName(field.getPath(), field.getName());
    fields.put(key, field);
  }

  public Json.JsonField get(final String path, final String name) {
    final String key = fieldFullName(path, name);
    return fields.get(key);
  }

  public boolean containsKey(final String path, final String name) {
    final String key = fieldFullName(path, name);
    return fields.containsKey(key);
  }
}
