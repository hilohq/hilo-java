package com.hilohq.hilo.structure_learning.parsing.json.entity;

import org.apache.commons.lang3.StringUtils;

public final class JsonPropertyPath {
  private final String path;
  private final String name;
  private final String fullname;

  public JsonPropertyPath(final String path, final String name) {
    this.path = path;
    this.name = name;
    this.fullname = JsonUtils.fullname(path, name);
  }

  public String path() {
    return path;
  }

  public String name() {
    return name;
  }

  public String fullname() {
    return fullname;
  }

  @Override
  public int hashCode() {
    return fullname.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null) {
      return false;
    }

    if (o == this) {
      return true;
    }

    if (!o.getClass().equals(JsonPropertyPath.class)) {
      return false;
    }

    final var other = (JsonPropertyPath) o;
    return StringUtils.equals(other.path(), path)
        && StringUtils.equals(other.name, name)
        && StringUtils.equals(other.fullname, fullname);
  }
}
