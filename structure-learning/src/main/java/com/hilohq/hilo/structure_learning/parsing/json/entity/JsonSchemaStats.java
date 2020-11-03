package com.hilohq.hilo.structure_learning.parsing.json.entity;

import static com.hilohq.hilo.structure_learning.parsing.json.entity.JsonUtils.fieldFullName;

import com.hilohq.hilo.protobufs.parsing.Json;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonSchemaStats implements Serializable {

  private final Map<String, JsonFieldStats> fields;

  public JsonSchemaStats() {
    this(new HashMap<>());
  }

  public JsonSchemaStats(final Map<String, JsonFieldStats> fields) {
    this.fields = fields;
  }

  public Map<String, JsonFieldStats> fields() {
    return Collections.unmodifiableMap(fields);
  }

  public boolean containsKey(final String key) {
    return fields.containsKey(key);
  }

  public void put(final JsonFieldStats field) {
    final String key = fieldFullName(field.path(), field.name());
    if (fields.containsKey(key)) {
      final var originalField = fields.get(key);
      originalField.mergeFrom(field);

    } else {
      fields.put(key, field);
    }
  }

  public void put(final String path, final String name, final Json.JsonType type) {
    final var field = new JsonFieldStats(path, name);
    field.observe(type);
    put(field);
  }

  public JsonFieldStats get(final String path, final String name) {
    final String key = fieldFullName(path, name);
    return fields.get(key);
  }
}
