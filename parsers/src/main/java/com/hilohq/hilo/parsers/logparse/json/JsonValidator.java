package com.hilohq.hilo.parsers.logparse.json;

import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.parsers.logparse.log.TypeInfo;
import com.hilohq.hilo.parsers.value.ValueType;

public final class JsonValidator {

  private final TypeInfo<JsonFieldKey, JsonType> typeInfo;

  public JsonValidator(final TypeInfo<JsonFieldKey, JsonType> typeInfo) {
    this.typeInfo = typeInfo;
  }

  private static JsonType mapValueTypeToJson(final ValueType type) {
    switch (type) {
      case Boolean:
        return JsonType.BOOLEAN;
      case Null:
        return JsonType.NULL;
      case String:
        return JsonType.STRING;
      case Bytes:
        return JsonType.BINARY;
      case Int:
      case Long:
      case Double:
      case Float:
        return JsonType.NUMBER;
      default:
        return null;
    }
  }

  public FieldsMap<JsonFieldKey> validate(final FieldsMap<JsonFieldKey> fields) {
    final var validatedFields = new FieldsMap<JsonFieldKey>();
    for (final var fieldEntry : fields.entrySet()) {
      if (!typeInfo.containsKey(fieldEntry.getKey())) {
        continue;
      }

      final var expectedJsonType = typeInfo.get(fieldEntry.getKey());
      final var receivedJsonType = mapValueTypeToJson(fieldEntry.getValue().type());
      if (!expectedJsonType.equals(receivedJsonType)) {
        continue;
      }

      validatedFields.put(fieldEntry.getKey(), fieldEntry.getValue());
    }

    return validatedFields;
  }
}
