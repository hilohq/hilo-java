package com.hilohq.hilo.parsers.logparse.json;

import static com.hilohq.hilo.parsers.logparse.json.JsonUtils.fullname;

import com.hilohq.hilo.parsers.field.FieldKey;
import org.apache.commons.lang3.StringUtils;

public final class JsonFieldKey implements FieldKey {

  private final String path;
  private final String name;
  private final String key;

  public JsonFieldKey(final String name) {
    this("", name);
  }

  public JsonFieldKey(final String path, final String name) {
    this.path = path;
    this.name = name;
    this.key = fullname(path, name);
  }

  public String path() {
    return path;
  }

  public String name() {
    return name;
  }

  @Override
  public String key() {
    return key;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null) {
      return false;
    }

    if (o == this) {
      return true;
    }

    if (!o.getClass().equals(JsonFieldKey.class)) {
      return false;
    }

    final var other = (JsonFieldKey) o;
    return StringUtils.equals(other.key(), key);
  }

  public int hashCode() {
    return key.hashCode();
  }
}
