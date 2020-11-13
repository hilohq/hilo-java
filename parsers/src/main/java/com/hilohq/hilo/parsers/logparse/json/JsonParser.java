package com.hilohq.hilo.parsers.logparse.json;

import static com.hilohq.hilo.parsers.logparse.json.JsonUtils.fullname;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hilohq.hilo.parsers.field.FieldsMap;
import com.hilohq.hilo.parsers.logparse.log.LogParser;
import com.hilohq.hilo.parsers.value.*;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class JsonParser implements LogParser<JsonFieldKey> {
  private final ObjectMapper mapper;

  public JsonParser() {
    this.mapper = new ObjectMapper();
  }

  private static BytesValue createBytesValue(final JsonNode node) {
    try {
      return new BytesValue(node.binaryValue());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Value createNumberValue(final JsonNode node) {
    if (node.isInt()) {
      return new IntValue(node.intValue());
    } else if (node.isLong()) {
      return new LongValue(node.longValue());
    } else if (node.isFloat()) {
      return new FloatValue(node.floatValue());
    } else if (node.isDouble()) {
      return new DoubleValue(node.doubleValue());
    } else {
      throw new IllegalArgumentException("Unexpected number value node " + node.getNodeType());
    }
  }

  private static void parseNodeObject(
      final JsonNode node,
      final String path,
      final String name,
      final FieldsMap<JsonFieldKey> fields) {
    final var it = node.fields();
    while (it.hasNext()) {
      final var fieldEntry = it.next();
      parseNode(fieldEntry.getValue(), fullname(path, name), fieldEntry.getKey(), fields);
    }
  }

  private static void parseNode(
      final JsonNode node,
      final String path,
      final String name,
      final FieldsMap<JsonFieldKey> fields) {
    final var key = new JsonFieldKey(path, name);
    switch (node.getNodeType()) {
      case BOOLEAN:
        fields.put(key, new BooleanValue(node.asBoolean()));
        break;
      case NULL:
        fields.put(key, new NullValue());
        break;
      case STRING:
        fields.put(key, new StringValue(node.asText()));
        break;
      case ARRAY:
        fields.put(key, new StringValue(node.toString()));
        break;
      case BINARY:
        fields.put(key, createBytesValue(node));
        break;
      case NUMBER:
        fields.put(key, createNumberValue(node));
        break;
      case OBJECT:
        parseNodeObject(node, path, name, fields);
        break;
      case POJO:
        throw new IllegalArgumentException("unsupported node type " + node.getNodeType());
      case MISSING:
        break;
      default:
        throw new IllegalArgumentException("unknown node type " + node.getNodeType());
    }
  }

  @Override
  public FieldsMap<JsonFieldKey> parse(final String log) {
    try {
      final var node = mapper.readTree(log);
      final var fields = new FieldsMap<JsonFieldKey>();
      parseNode(node, "", "", fields);
      return fields;

    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }
}
