package com.hilohq.hilo.parsers.logparse.log;

import static org.junit.Assert.assertEquals;

import com.hilohq.hilo.parsers.logparse.json.JsonFieldKey;
import com.hilohq.hilo.parsers.logparse.json.JsonProtobufObjectMapper;
import com.hilohq.hilo.parsers.logparse.json.JsonType;
import org.junit.Test;

public final class TypeInfoStatsTest {

  @Test
  public void testHistogramAccumulatesCounts() {
    final var histogram = new TypeInfoHistogram<JsonType>();
    histogram.observe(JsonType.STRING);
    histogram.observe(JsonType.STRING);
    histogram.observe(JsonType.NULL, 3);

    assertEquals(2, histogram.count(JsonType.STRING));
    assertEquals(3, histogram.count(JsonType.NULL));
    assertEquals(0, histogram.count(JsonType.NUMBER));
  }

  @Test
  public void testMergeSumsBothHistograms() {
    final var a = new TypeInfoFieldStats<JsonType>();
    a.observe(JsonType.STRING);
    a.observe(JsonType.NULL);
    final var b = new TypeInfoFieldStats<JsonType>();
    b.observe(JsonType.STRING);
    b.observe(JsonType.STRING);

    final var merged = a.merge(b);

    assertEquals(3, merged.typeInfoHistogram().count(JsonType.STRING));
    assertEquals(1, merged.typeInfoHistogram().count(JsonType.NULL));
    // merge returns a new instance and leaves the inputs untouched
    assertEquals(1, a.typeInfoHistogram().count(JsonType.STRING));
    assertEquals(2, b.typeInfoHistogram().count(JsonType.STRING));
  }

  @Test
  public void testPreferPicksTheMostFrequentType() {
    final var stats = new TypeInfoFieldStats<JsonType>();
    stats.typeInfoHistogram().observe(JsonType.STRING, 214);
    stats.typeInfoHistogram().observe(JsonType.NULL, 3);

    assertEquals(JsonType.STRING, new TypeInfoPrefer<JsonType>().fromStats(stats, JsonType.NULL));
  }

  @Test
  public void testPreferReturnsDefaultWhenNothingWasObserved() {
    assertEquals(
        JsonType.NULL,
        new TypeInfoPrefer<JsonType>().fromStats(new TypeInfoFieldStats<>(), JsonType.NULL));
  }

  @Test
  public void testStatsAreTrackedPerField() {
    final var stats = new TypeInfoStats<JsonFieldKey, JsonType>();
    stats.observe(new JsonFieldKey("a"), JsonType.NUMBER);
    stats.observe(new JsonFieldKey("a"), JsonType.NUMBER);
    stats.observe(new JsonFieldKey("b"), JsonType.STRING);

    assertEquals(2, stats.fieldStats().size());
    for (final var entry : stats.fieldStats()) {
      if (entry.getKey().key().equals("a")) {
        assertEquals(2, entry.getValue().typeInfoHistogram().count(JsonType.NUMBER));
      } else {
        assertEquals(1, entry.getValue().typeInfoHistogram().count(JsonType.STRING));
      }
    }
  }

  @Test
  public void testProtobufRoundTripPreservesCounts() {
    final var histogram = new TypeInfoHistogram<JsonType>();
    for (final var type : JsonType.values()) {
      histogram.observe(type, type.ordinal() + 1);
    }
    final var mapper = new JsonProtobufObjectMapper();

    final var proto = mapper.toProtobuf(new TypeInfoFieldStats<>(histogram));
    final var restored = mapper.fromProtobuf(proto).typeInfoHistogram();

    assertEquals(3, proto.getTypeHistogram().getStringCount());
    for (final var type : JsonType.values()) {
      assertEquals(type.ordinal() + 1, restored.count(type));
    }
  }
}
