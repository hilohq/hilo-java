package com.hilohq.hilo.structure_learning.parsing.json;

import static com.hilohq.hilo.protobufs.schema.Schema.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.protobufs.schema.Json;
import com.hilohq.hilo.structure_learning.parsing.text.TextLineBatch;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Test;

public final class JsonSchemaGenerateTest {

  private SchemaEvent eventWithFieldStats(final List<SchemaEvent> events, final String fieldName) {
    for (final var event : events) {
      if (event.getJson().hasFieldStats()
          && event.getJson().getFieldStats().getName().equals(fieldName)) {
        return event;
      }
    }

    throw new IllegalArgumentException("no event with field create");
  }

  private static Json.JsonSchemaEvent createJsonEvent(
      final String name, final Json.JsonTypeHistogram histogram) {
    return Json.JsonSchemaEvent.newBuilder()
        .setFieldStats(
            Json.JsonSchemaEvent.FieldStats.newBuilder()
                .setName(name)
                .setFieldStats(Json.JsonFieldStats.newBuilder().setTypeHistogram(histogram).build())
                .build())
        .build();
  }

  @Test
  public void testGenerateSchemaOneBatchCreateEvents() throws Exception {
    final var generate = new JsonSchemaGenerate();
    final var batch =
        TextLineBatch.fromLines(
            "{\"integer\": 1, \"string\": \"hello\"}", "{\"integer\": 2, \"string\": \"bye\"}");
    final var events = new ArrayList<SchemaEvent>();
    final var collector = new ListCollector<>(events);

    generate.processElement(batch, null, collector);

    assertEquals(3, events.size());
    assertEquals(
        eventWithFieldStats(events, "integer").getJson(),
        createJsonEvent("integer", Json.JsonTypeHistogram.newBuilder().setIntegerCount(2).build()));
    assertEquals(
        eventWithFieldStats(events, "string").getJson(),
        createJsonEvent("string", Json.JsonTypeHistogram.newBuilder().setStringCount(2).build()));
    assertEquals(
        events.get(2).getJson(),
        Json.JsonSchemaEvent.newBuilder()
            .setParseStats(
                Json.JsonSchemaEvent.ParseStats.newBuilder()
                    .setStats(
                        Json.JsonParseStats.newBuilder()
                            .setParseAttemptCount(2)
                            .setParseSuccessCount(2)
                            .build())
                    .build())
            .build());
  }

  @Test
  public void testGenerateSchemaParseErrorEvent() throws Exception {
    final var generate = new JsonSchemaGenerate();
    final var batch = TextLineBatch.fromLines("not a json");
    final var events = new ArrayList<SchemaEvent>();
    final var collector = new ListCollector<>(events);

    generate.processElement(batch, null, collector);

    assertEquals(2, events.size());
    assertEquals(
        events.get(0).getJson().getParseErrors().getParseErrors(0).getContent(), "not a json");
    assertEquals(
        events.get(0).getJson().getParseErrors().getParseErrors(0).getReason(), "Invalid JSON");
    assertTrue(
        events
            .get(0)
            .getJson()
            .getParseErrors()
            .getParseErrors(0)
            .getDescription()
            .contains("Unrecognized token"));
  }

  @Test
  public void testGenerateSchemaOneBatchMultipleTypes() throws Exception {
    final var generate = new JsonSchemaGenerate();
    final var batch =
        TextLineBatch.fromLines(
            "{\"integer\": true, \"string\": \"hello\"}",
            "{\"integer\": false, \"string\": \"bye\"}",
            "{\"integer\": 1, \"string\": \"hello\"}",
            "{\"integer\": 2, \"string\": \"bye\"}");
    final var events = new ArrayList<SchemaEvent>();
    final var collector = new ListCollector<>(events);

    generate.processElement(batch, null, collector);

    assertEquals(3, events.size());

    assertEquals(
        eventWithFieldStats(events, "integer").getJson(),
        createJsonEvent(
            "integer",
            Json.JsonTypeHistogram.newBuilder().setIntegerCount(2).setBooleanCount(2).build()));
    assertEquals(
        eventWithFieldStats(events, "string").getJson(),
        createJsonEvent("string", Json.JsonTypeHistogram.newBuilder().setStringCount(4).build()));
    assertEquals(
        events.get(2).getJson(),
        Json.JsonSchemaEvent.newBuilder()
            .setParseStats(
                Json.JsonSchemaEvent.ParseStats.newBuilder()
                    .setStats(
                        Json.JsonParseStats.newBuilder()
                            .setParseAttemptCount(4)
                            .setParseSuccessCount(4)
                            .build())
                    .build())
            .build());
  }
}
