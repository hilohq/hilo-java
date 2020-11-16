package com.hilohq.integration_tests.resources;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ResourceUtils {

  private ResourceUtils() {}

  public static void createFolders(final String folderPath) {
    final var absolutePath = Paths.get(folderPath).toAbsolutePath();
    if (absolutePath.toFile().exists()) {
      return;
    }

    final var parentPath = absolutePath.getParent();
    final var parentFile = parentPath.toFile();
    if (!parentFile.exists()) {
      createFolders(parentPath.toString());
    }

    final boolean ok = absolutePath.toFile().mkdir();
    if (!ok) {
      throw new IllegalStateException("failed to create folder " + absolutePath.toString());
    }
  }

  public static void copyResource(final String resourcePath, final String targetPath)
      throws Exception {
    final var resource = ResourceUtils.class.getClassLoader().getResource(resourcePath);
    if (resource == null) {
      throw new IllegalArgumentException("invalid resource " + resourcePath);
    }

    final var path = Paths.get(resource.toURI()).toAbsolutePath();
    createFolders(Paths.get(targetPath).getParent().toString());
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

  public static <T extends Message> T readFromResource(final String resourcePath, final T type)
      throws Exception {
    final var builder = type.newBuilderForType();
    final var schemaString = readStringResource(resourcePath);
    JsonFormat.parser().merge(schemaString, builder);
    return (T) builder.build();
  }
}
