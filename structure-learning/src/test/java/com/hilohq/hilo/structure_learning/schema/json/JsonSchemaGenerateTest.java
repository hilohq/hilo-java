package com.hilohq.hilo.structure_learning.schema.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.protobufs.structure.schema.Json;
import com.hilohq.hilo.protobufs.structure.schema.Schema;
import com.hilohq.hilo.structure_learning.Samples;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Test;

/**
 * Runs the two schema-learning reducers (JsonSchemaStatsGenerate followed by a group-by-key and
 * JsonSchemaUpdateGenerate) in-process, the same way JsonSchemaGenerateStages wires them in Flink.
 */
public final class JsonSchemaGenerateTest {

  private static List<Schema.SchemaEvent> generateStats(final List<String> logs) throws Exception {
    final var events = new ArrayList<Schema.SchemaEvent>();
    new JsonSchemaStatsGenerate(Samples.PROJECT_ID, Samples.SOURCE_ID, Samples.SCHEMA_ID)
        .reduce(logs, new ListCollector<>(events));
    return events;
  }

  private static List<Schema.SchemaUpdate> generateUpdates(final List<String> logs)
      throws Exception {
    final var keySelector = new JsonFieldStatsKeySelector();
    final var groups = new HashMap<String, List<Schema.SchemaEvent>>();
    for (final var event : generateStats(logs)) {
      groups.computeIfAbsent(keySelector.getKey(event), k -> new ArrayList<>()).add(event);
    }

    final var updates = new ArrayList<Schema.SchemaUpdate>();
    final var generate = new JsonSchemaUpdateGenerate();
    for (final var group : groups.values()) {
      generate.reduce(group, new ListCollector<>(updates));
    }
    return updates;
  }

  private static Map<String, Json.JsonField> fields(final List<Schema.SchemaUpdate> updates) {
    final var fields = new HashMap<String, Json.JsonField>();
    for (final var update : updates) {
      if (update.getJson().hasField()) {
        fields.put(update.getJson().getField().getKey(), update.getJson().getField());
      }
    }
    return fields;
  }

  private static long createLogs(final List<Schema.SchemaUpdate> updates) {
    return updates.stream()
        .filter(u -> u.getJson().hasLog() && u.getJson().getLog().hasFieldCreate())
        .count();
  }

  @Test
  public void testStatsEventsCarryMetadataAndPerFieldTypeCounts() throws Exception {
    final var events =
        generateStats(Arrays.asList("{\"a\":1,\"b\":\"x\"}", "{\"a\":2,\"b\":null}"));

    assertEquals(2, events.size());
    for (final var event : events) {
      assertEquals(Samples.SCHEMA_ID, event.getMetadata().getSchemaId());
      assertEquals(Samples.PROJECT_ID, event.getMetadata().getProjectId());
      assertEquals(Samples.SOURCE_ID, event.getMetadata().getSourceId());

      final var stats = event.getJson().getFieldStats();
      final var histogram = stats.getFieldStats().getTypeHistogram();
      if (stats.getKey().equals("a")) {
        assertEquals(2, histogram.getNumberCount());
      } else {
        assertEquals("b", stats.getKey());
        assertEquals(1, histogram.getStringCount());
        assertEquals(1, histogram.getNullCount());
      }
    }
  }

  @Test
  public void testMajorityTypeWinsAndNestedFieldsKeepTheirPath() throws Exception {
    final var updates =
        generateUpdates(
            Arrays.asList(
                "{\"to\":\"0x1\",\"tx\":{\"gas\":1}}",
                "{\"to\":\"0x2\",\"tx\":{\"gas\":2}}",
                "{\"to\":null,\"tx\":{\"gas\":3}}"));

    final var fields = fields(updates);
    assertEquals(2, fields.size());
    assertEquals(Json.JsonType.JSON_STRING, fields.get("to").getType());

    final var gas = fields.get("tx.gas");
    assertEquals("tx", gas.getPath());
    assertEquals("gas", gas.getName());
    assertEquals(Json.JsonType.JSON_NUMBER, gas.getType());
    assertEquals(3, gas.getStats().getTypeHistogram().getNumberCount());

    // a brand new schema emits one "field created" log per field
    assertEquals(2, createLogs(updates));
    for (final var update : updates) {
      if (update.getJson().hasLog()) {
        final var create = update.getJson().getLog().getFieldCreate();
        assertTrue(create.getKey().equals("to") || create.getKey().equals("tx.gas"));
      }
    }
  }

  @Test
  public void testInfersTheTransactionsSampleSchema() throws Exception {
    final var updates = generateUpdates(Samples.logs("transactions"));
    final var expected = Samples.schema("transactions");

    assertEquals(expected.getJson().getFieldsMap(), fields(updates));
    assertEquals(14, createLogs(updates));
    // "to" is null for contract-creation transactions, but string wins the vote
    assertEquals(3, fields(updates).get("to").getStats().getTypeHistogram().getNullCount());
  }

  @Test
  public void testInfersTheBlocksSampleSchema() throws Exception {
    final var updates = generateUpdates(Samples.logs("blocks"));
    final var expected = Samples.schema("blocks");

    assertEquals(expected.getJson().getFieldsMap(), fields(updates));
    assertEquals(20, createLogs(updates));
  }
}
