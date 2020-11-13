package com.hilohq.hilo.structure_learning.schema.json;

import static com.hilohq.hilo.structure_learning.schema.json.JsonProtobufUtils.mapFromProtoType;

import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonParser;
import com.hilohq.hilo.parsers.logparse.json.JsonType;
import com.hilohq.hilo.parsers.logparse.json.JsonValidator;
import com.hilohq.hilo.parsers.logparse.log.TypeInfo;
import com.hilohq.hilo.schema_service.service.MongoSchemaStore;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.functions.RichGroupReduceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public final class JsonSchemaValidate extends RichGroupReduceFunction<String, FieldsMap<FieldKey>> {
  private final String schemaId;
  private final String mongoUrl;
  private TypeInfo<JsonFieldKey, JsonType> typeInfo;

  public JsonSchemaValidate(final String mongoUrl, final String schemaId) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final var store = MongoSchemaStore.open(mongoUrl);
    final var schema = store.getSchema(schemaId).get(5, TimeUnit.SECONDS);
    if (!schema.hasJson()) {
      throw new IllegalStateException("JsonSchemaValidate only accepts json schemas");
    }

    store.close();

    final var types = new HashMap<JsonFieldKey, JsonType>();
    for (final var entry : schema.getJson().getFieldsMap().entrySet()) {
      final var key = new JsonFieldKey(entry.getValue().getPath(), entry.getValue().getName());
      types.put(key, mapFromProtoType(entry.getValue().getType()));
    }

    typeInfo = new TypeInfo<>(types);
  }

  @Override
  public void reduce(final Iterable<String> values, final Collector<FieldsMap<FieldKey>> out)
      throws Exception {
    final var parser = new JsonParser();
    final var validator = new JsonValidator(typeInfo);

    for (final var line : values) {
      final var fields = parser.parse(line);
      final var validatedFields = validator.validate(fields);
      final var result = new FieldsMap<>();
      for (final var entry : validatedFields.entrySet()) {
        result.put(entry.getKey(), entry.getValue());
      }
      out.collect(result);
    }
  }

  @Override
  public void close() throws Exception {}
}
