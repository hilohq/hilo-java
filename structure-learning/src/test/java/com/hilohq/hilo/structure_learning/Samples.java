package com.hilohq.hilo.structure_learning;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonParser;
import com.hilohq.hilo.parsers.logparse.json.JsonType;
import com.hilohq.hilo.parsers.logparse.json.JsonValidator;
import com.hilohq.hilo.parsers.logparse.log.TypeInfo;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.hilohq.hilo.structure_learning.schema.json.JsonProtobufUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads the sample datasets shared with the integration tests
 * (integration-tests/src/integration-test/resources/samples) so that unit tests can check the
 * learning stages against the same golden files without Flink, Mongo or Docker.
 */
public final class Samples {
  public static final String PROJECT_ID = "test-project";
  public static final String SOURCE_ID = "test-source";
  public static final String SCHEMA_ID = "test-schema";

  private static final Path SAMPLES_DIR =
      Path.of("..", "integration-tests", "src", "integration-test", "resources", "samples");

  private Samples() {}

  public static Path path(final String sample, final String file) {
    return SAMPLES_DIR.resolve(sample).resolve(file);
  }

  public static String read(final String sample, final String file) {
    try {
      return Files.readString(path(sample, file))
          .replace("$PROJECT_ID", PROJECT_ID)
          .replace("$SOURCE_ID", SOURCE_ID)
          .replace("$SCHEMA_ID", SCHEMA_ID);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static List<String> logs(final String sample) {
    return read(sample, "logs.txt")
        .lines()
        .filter(line -> !line.isBlank())
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public static <T extends Message> T readProto(
      final String sample, final String file, final T prototype) {
    try {
      final var builder = prototype.newBuilderForType();
      JsonFormat.parser().merge(read(sample, file), builder);
      return (T) builder.build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Schema.SchemaFieldsModel schema(final String sample) {
    return readProto(sample, "schema.json", Schema.SchemaFieldsModel.getDefaultInstance());
  }

  /**
   * Parses and validates the sample logs against the sample schema, mirroring what
   * JsonSchemaValidate does after loading the schema from Mongo.
   */
  public static List<Fields<FieldKey>> validatedFields(final String sample) {
    final var schema = schema(sample);
    final var types = new HashMap<JsonFieldKey, JsonType>();
    for (final var field : schema.getJson().getFieldsMap().values()) {
      types.put(
          new JsonFieldKey(field.getPath(), field.getName()),
          JsonProtobufUtils.mapFromProtoType(field.getType()));
    }

    final var parser = new JsonParser();
    final var validator = new JsonValidator(new TypeInfo<>(types));
    final var result = new ArrayList<Fields<FieldKey>>();
    for (final var line : logs(sample)) {
      final var map = new FieldsMap<FieldKey>();
      for (final var entry : validator.validate(parser.parse(line)).entrySet()) {
        map.put(entry.getKey(), entry.getValue());
      }
      result.add(new Fields<>(schema.getMetadata(), map));
    }
    return result;
  }
}
