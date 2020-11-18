package com.hilohq.hilo.structure_learning.schema.json;

import static com.hilohq.hilo.structure_learning.schema.json.JsonProtobufUtils.mapFromProtoType;

import com.hilohq.hilo.parsers.field.FieldKey;
import com.hilohq.hilo.parsers.field.Fields;
import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonParser;
import com.hilohq.hilo.parsers.logparse.json.JsonType;
import com.hilohq.hilo.parsers.logparse.json.JsonValidator;
import com.hilohq.hilo.parsers.logparse.log.TypeInfo;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.hilohq.hilo.schema_service.service.MongoSchemaStore;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.functions.AbstractRichFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public final class JsonSchemaValidate extends AbstractRichFunction {

  private final String schemaId;
  private final String mongoUrl;
  private TypeInfo<JsonFieldKey, JsonType> typeInfo;
  private Schema.SchemaFieldsModel schema;
  private Schema.SchemaMetadata metadata;
  private JsonValidator validator;
  private JsonParser parser;

  public JsonSchemaValidate(final String mongoUrl, final String schemaId) {
    this.mongoUrl = mongoUrl;
    this.schemaId = schemaId;
  }

  @Override
  public void open(final Configuration parameters) throws Exception {
    final var store = MongoSchemaStore.open(mongoUrl);
    schema = store.getSchema(schemaId).get(5, TimeUnit.SECONDS);
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
    parser = new JsonParser();
    validator = new JsonValidator(typeInfo);
    metadata = schema.getMetadata();
  }

  public void processElement(final String value, final Collector<Fields<FieldKey>> out) {
    final var fields = parser.parse(value);
    final var validatedFields = validator.validate(fields);
    final var result = new FieldsMap<>();
    for (final var entry : validatedFields.entrySet()) {
      result.put(entry.getKey(), entry.getValue());
    }
    out.collect(new Fields<>(metadata, result));
  }

  @Override
  public void close() throws Exception {}
}
