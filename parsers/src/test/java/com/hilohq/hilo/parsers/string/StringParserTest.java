package com.hilohq.hilo.parsers.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.parsers.value.ValueType;
import org.junit.Test;

public final class StringParserTest {

  private final StringParser parser = new StringParser();

  @Test
  public void testParsesBooleansCaseInsensitively() {
    final var t = parser.parse("TRUE");
    assertEquals(ValueType.Boolean, t.type());
    assertTrue(t.booleanValue());

    final var f = parser.parse("false");
    assertEquals(ValueType.Boolean, f.type());
    assertFalse(f.booleanValue());
    assertFalse(f.isNumeric());
  }

  @Test
  public void testPrefersIntForValuesThatFitInAnInt() {
    final var value = parser.parse("63000");
    assertEquals(ValueType.Int, value.type());
    assertEquals(63000, value.intValue());
    assertEquals(63000.0, value.numericValue(), 0.0);
  }

  @Test
  public void testFallsBackToLongWhenIntOverflows() {
    // gasPrice in the transactions sample is a JSON string holding a value larger than an int.
    final var value = parser.parse("80525500000");
    assertEquals(ValueType.Long, value.type());
    assertEquals(80525500000L, value.longValue());
  }

  @Test
  public void testFallsBackToFloatWhenLongOverflows() {
    // "value" in the transactions sample (wei) does not fit in a long.
    final var value = parser.parse("100000000000000000000");
    assertEquals(ValueType.Float, value.type());
    assertTrue(value.isNumeric());
    assertEquals(1e20f, value.floatValue(), 1e13f);
  }

  @Test
  public void testParsesDecimalsAsFloat() {
    final var value = parser.parse("1.5");
    assertEquals(ValueType.Float, value.type());
    assertEquals(1.5f, value.floatValue(), 0.0f);
  }

  @Test
  public void testFallsBackToStringForNonNumericValues() {
    final var hex = parser.parse("0x1c");
    assertEquals(ValueType.String, hex.type());
    assertEquals("0x1c", hex.stringValue());
    assertFalse(hex.isNumeric());

    final var empty = parser.parse("");
    assertEquals(ValueType.String, empty.type());
    assertEquals("", empty.stringValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBooleanRejectsNonBooleans() {
    parser.parseBoolean("yes");
  }
}
