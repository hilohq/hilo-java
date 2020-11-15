package com.hilohq.integration_tests.resources;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ResourceUtils {

  private ResourceUtils() {}

  public static void copyResource(final String resourcePath, final String targetPath)
      throws Exception {
    final var resource = ResourceUtils.class.getClassLoader().getResource(resourcePath);
    if (resource == null) {
      throw new IllegalArgumentException("invalid resource " + resourcePath);
    }

    final var path = Paths.get(resource.toURI()).toAbsolutePath();
    final var target = new BufferedOutputStream(new FileOutputStream(targetPath));
    Files.copy(path, target);
    target.flush();
    target.close();
  }

  public static String readStringResource(final String resourcePath) throws Exception {
    final var resource = ResourceUtils.class.getClassLoader().getResource(resourcePath);
    if (resource == null) {
      throw new IllegalArgumentException("invalid resource " + resourcePath);
    }

    final var path = Paths.get(resource.toURI()).toAbsolutePath();
    return Files.readString(path);
  }
}
