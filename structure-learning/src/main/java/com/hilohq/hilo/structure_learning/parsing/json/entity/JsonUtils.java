package com.hilohq.hilo.structure_learning.parsing.json.entity;

import org.apache.commons.lang3.StringUtils;

public final class JsonUtils {
  private JsonUtils() {}

  public static String fullname(final String path, final String name) {
    if (StringUtils.isEmpty(path)) {
      return name;

    } else if (StringUtils.isEmpty(path)) {
      return path;

    } else {
      return String.join(".", path, name);
    }
  }
}
