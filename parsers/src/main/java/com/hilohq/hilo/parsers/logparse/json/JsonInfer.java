package com.hilohq.hilo.parsers.logparse.json;

import static com.hilohq.hilo.parsers.logparse.json.JsonUtils.fullname;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hilohq.hilo.parsers.logparse.log.Infer;
import com.hilohq.hilo.parsers.logparse.log.TypeInfo;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public final class JsonInfer implements Infer<JsonFieldKey, JsonType> {
  private final ObjectMapper mapper;

  public JsonInfer() {
    this.mapper = new ObjectMapper();
  }

  private static void parseNodeObject(
      final JsonNode node,
      final String path,
      final String name,
      final Map<JsonFieldKey, JsonType> fields) {
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
      final Map<JsonFieldKey, JsonType> fields) {
    final var key = new JsonFieldKey(path, name);
    switch (node.getNodeType()) {
      case BOOLEAN:
        fields.put(key, JsonType.BOOLEAN);
        break;
      case NULL:
        fields.put(key, JsonType.NULL);
        break;
      case STRING:
        fields.put(key, JsonType.STRING);
        break;
      case ARRAY:
        fields.put(key, JsonType.ARRAY);
        break;
      case BINARY:
        fields.put(key, JsonType.BINARY);
        break;
      case NUMBER:
        fields.put(key, JsonType.NUMBER);
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
  public TypeInfo<JsonFieldKey, JsonType> infer(final String log) {
    try {
      final var node = mapper.readTree(log);
      final var fields = new HashMap<JsonFieldKey, JsonType>();
      parseNode(node, "", "", fields);
      return new TypeInfo<>(fields);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }
}
