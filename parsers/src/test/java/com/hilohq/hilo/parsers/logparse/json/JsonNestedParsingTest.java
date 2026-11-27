package com.hilohq.hilo.parsers.logparse.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.parsers.value.ValueType;
import org.junit.Test;

/** Covers nested objects, value-type mapping and field keys for the JSON parser and inferrer. */
public final class JsonNestedParsingTest {

  private static final String NESTED_LOG =
      "{\"service\":\"api\",\"ok\":true,\"latency\":{\"p50\":1.25,\"p99\":{\"ms\":250}},"
          + "\"user\":null,\"tags\":[\"a\",\"b\"]}";

  @Test
  public void testFullnameJoinsPathAndName() {
    assertEquals("name", JsonUtils.fullname("", "name"));
    assertEquals("name", JsonUtils.fullname(null, "name"));
    assertEquals("a.b", JsonUtils.fullname("a", "b"));
    assertEquals("a.b.c", JsonUtils.fullname("a.b", "c"));
  }

  @Test
  public void testFieldKeyEqualityIsBasedOnFullKey() {
    final var key = new JsonFieldKey("latency.p99", "ms");
    assertEquals("latency.p99", key.path());
    assertEquals("ms", key.name());
    assertEquals("latency.p99.ms", key.key());

    final var sameKey = new JsonFieldKey("latency", "p99.ms");
    assertEquals(key, sameKey);
    assertEquals(key.hashCode(), sameKey.hashCode());
    assertNotEquals(key, new JsonFieldKey("latency", "p99"));
    assertNotEquals(key, null);
  }

  @Test
  public void testParserFlattensNestedObjectsIntoDottedKeys() {
    final var fields = new JsonParser().parse(NESTED_LOG);

    assertEquals(6, fields.size());
    assertEquals("api", fields.get(new JsonFieldKey("service")).stringValue());
    assertTrue(fields.get(new JsonFieldKey("ok")).booleanValue());

    final var p50 = fields.get(new JsonFieldKey("latency", "p50"));
    assertEquals(ValueType.Double, p50.type());
    assertEquals(1.25, p50.doubleValue(), 0.0);

    final var p99 = fields.get(new JsonFieldKey("latency.p99", "ms"));
    assertEquals(ValueType.Int, p99.type());
    assertEquals(250, p99.intValue());

    assertEquals(ValueType.Null, fields.get(new JsonFieldKey("user")).type());

    // arrays are kept as their raw JSON string representation
    final var tags = fields.get(new JsonFieldKey("tags"));
    assertEquals(ValueType.String, tags.type());
    assertEquals("[\"a\",\"b\"]", tags.stringValue());

    // intermediate objects are not emitted as fields themselves
    assertEquals(null, fields.get(new JsonFieldKey("latency")));
  }

  @Test
  public void testInferProducesOneTypePerLeafField() {
    final var typeInfo = new JsonInfer().infer(NESTED_LOG);

    assertEquals(6, typeInfo.size());
    assertEquals(JsonType.STRING, typeInfo.get(new JsonFieldKey("service")));
    assertEquals(JsonType.BOOLEAN, typeInfo.get(new JsonFieldKey("ok")));
    assertEquals(JsonType.NUMBER, typeInfo.get(new JsonFieldKey("latency", "p50")));
    assertEquals(JsonType.NUMBER, typeInfo.get(new JsonFieldKey("latency.p99", "ms")));
    assertEquals(JsonType.NULL, typeInfo.get(new JsonFieldKey("user")));
    assertEquals(JsonType.ARRAY, typeInfo.get(new JsonFieldKey("tags")));
    assertFalse(typeInfo.containsKey(new JsonFieldKey("latency")));
  }

  @Test(expected = java.io.UncheckedIOException.class)
  public void testParserRejectsMalformedJson() {
    new JsonParser().parse("{\"a\": ");
  }
}
