package com.hilohq.hilo.parsers.logparse.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.hilohq.hilo.parsers.logparse.log.TypeInfo;
import java.util.HashMap;
import org.junit.Test;

public final class JsonValidatorTest {

  private static JsonValidator validatorFor(final String schemaLikeLog) {
    // Learn the expected types from a reference log line, the same way a schema would.
    return new JsonValidator(new JsonInfer().infer(schemaLikeLog));
  }

  @Test
  public void testKeepsFieldsMatchingTheSchemaAndDropsUnknownOnes() {
    final var validator = validatorFor("{\"to\":\"0xabc\",\"gas\":21000,\"ok\":true}");
    final var fields =
        new JsonParser().parse("{\"to\":\"0xdef\",\"gas\":50000,\"ok\":false,\"extra\":1}");

    final var validated = validator.validate(fields);

    assertEquals(3, validated.size());
    assertEquals("0xdef", validated.get(new JsonFieldKey("to")).stringValue());
    assertEquals(50000, validated.get(new JsonFieldKey("gas")).intValue());
    assertNull(validated.get(new JsonFieldKey("extra")));
  }

  @Test
  public void testDropsFieldsWhoseTypeDoesNotMatch() {
    // In the transactions sample "to" is a string in most lines but null in a few.
    final var validator = validatorFor("{\"to\":\"0xabc\",\"gas\":21000}");
    final var fields = new JsonParser().parse("{\"to\":null,\"gas\":\"not-a-number\"}");

    final var validated = validator.validate(fields);

    assertEquals(0, validated.size());
  }

  @Test
  public void testAllNumericValueTypesMatchTheJsonNumberType() {
    final var types = new HashMap<JsonFieldKey, JsonType>();
    types.put(new JsonFieldKey("i"), JsonType.NUMBER);
    types.put(new JsonFieldKey("l"), JsonType.NUMBER);
    types.put(new JsonFieldKey("d"), JsonType.NUMBER);
    final var validator = new JsonValidator(new TypeInfo<>(types));

    final var validated =
        validator.validate(new JsonParser().parse("{\"i\":1,\"l\":12345678901234,\"d\":0.5}"));

    assertEquals(3, validated.size());
  }
}
