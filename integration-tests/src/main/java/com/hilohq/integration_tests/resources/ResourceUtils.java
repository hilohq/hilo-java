package com.hilohq.integration_tests.resources;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

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
    return readStringResource(resourcePath, Collections.emptyMap());
  }

  public static String readStringResource(
      final String resourcePath, final Map<String, String> substitutions) throws Exception {
    final var resource = ResourceUtils.class.getClassLoader().getResource(resourcePath);
    if (resource == null) {
      throw new IllegalArgumentException("invalid resource " + resourcePath);
    }

    final var path = Paths.get(resource.toURI()).toAbsolutePath();
    var stringResource = Files.readString(path);
    for (final var entry : substitutions.entrySet()) {
      stringResource = stringResource.replace(entry.getKey(), entry.getValue());
    }

    return stringResource;
  }

  public static <T extends Message> T readFromResource(final String resourcePath, final T type)
      throws Exception {
    return readFromResource(resourcePath, type, Collections.emptyMap());
  }

  public static <T extends Message> T readFromResource(
      final String resourcePath, final T type, final Map<String, String> substitutions)
      throws Exception {
    final var builder = type.newBuilderForType();
    var schemaString = readStringResource(resourcePath, substitutions);
    JsonFormat.parser().merge(schemaString, builder);
    return (T) builder.build();
  }
}
