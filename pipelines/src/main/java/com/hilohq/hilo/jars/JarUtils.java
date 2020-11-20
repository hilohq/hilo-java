package com.hilohq.hilo.jars;

import java.net.URISyntaxException;
import java.nio.file.Path;

public final class JarUtils {

  private JarUtils() {}

  public static Path jarPathForModule(final String module) {
    try {
      var basePath =
          Path.of(JarUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI())
              .toAbsolutePath();

      if (!basePath.toString().contains("/build")) {
        throw new IllegalArgumentException(
            "unknown base path " + basePath + " for module " + module);
      }

      while (!basePath.toString().equals("/")
          && !basePath.getFileName().toString().equals(module)) {
        basePath = basePath.getParent();
        if (basePath.endsWith(module)) {
          break;
        }

        if (Path.of(basePath.toString(), module).toFile().exists()) {
          basePath = Path.of(basePath.toString(), module);
          break;
        }
      }

      basePath = Path.of(basePath.toString(), "build");
      if (!basePath.getFileName().toString().equals("build")) {
        throw new IllegalStateException("failed to find build directory");
      }

      final var jarPath = Path.of(basePath.toString(), "libs/" + module + "-0.0.1-all.jar");
      if (!jarPath.toFile().exists()) {
        throw new IllegalArgumentException(
            "jar file " + jarPath.toString() + " for module " + module + " does not exist");
      }

      return jarPath;

    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
