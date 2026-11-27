package com.hilohq.hilo.structure_learning.features;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.connectors_mongo.exceptions.MongoFindException;
import com.hilohq.hilo.features.stats.feature.FeatureStats;
import com.hilohq.hilo.features_service.service.FeaturesStore;
import com.hilohq.hilo.parsers.value.IntValue;
import com.hilohq.hilo.parsers.value.StringValue;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Test;

public final class FeaturesUpdateGenerateTest {
  private static final String SCHEMA_ID = "schema";
  private static final FeatureOuterClass.FeaturesMetadata METADATA =
      FeatureOuterClass.FeaturesMetadata.newBuilder()
          .setSchemaId(SCHEMA_ID)
          .setProjectId("project")
          .setSourceId("source")
          .build();

  private static final FeatureOuterClass.FeatureClass COUNTER =
      FeatureOuterClass.FeatureClass.newBuilder()
          .setCounter(FeatureOuterClass.FeatureClass.Counter.getDefaultInstance())
          .build();
  private static final FeatureOuterClass.FeatureClass GAUGE =
      FeatureOuterClass.FeatureClass.newBuilder()
          .setGauge(FeatureOuterClass.FeatureClass.Gauge.getDefaultInstance())
          .build();
  private static final FeatureOuterClass.FeatureClass EPHEMERAL =
      FeatureOuterClass.FeatureClass.newBuilder()
          .setEphemeral(FeatureOuterClass.FeatureClass.Ephemeral.getDefaultInstance())
          .build();

  /** Stats of a field that increases by one per line: start, start + 1, ... */
  private static FeatureStats counterStats(final int start, final int count) {
    final var stats = new FeatureStats();
    for (int i = start; i < start + count; i++) {
      stats.observe(new IntValue(i));
    }
    return stats;
  }

  private static FeatureStats stringStats(final String... values) {
    final var stats = new FeatureStats();
    for (final var value : values) {
      stats.observe(new StringValue(value));
    }
    return stats;
  }

  private static FeatureOuterClass.FeaturesEvent event(final String key, final FeatureStats stats) {
    return FeatureOuterClass.FeaturesEvent.newBuilder()
        .setMetadata(METADATA)
        .setStats(
            FeatureOuterClass.StatsEvent.newBuilder().setKey(key).setFeatureStats(stats.snapshot()))
        .build();
  }

  private static FeatureOuterClass.Feature stored(
      final String key, final FeatureStats stats, final FeatureOuterClass.FeatureClass cls) {
    return FeatureOuterClass.Feature.newBuilder()
        .setKey(key)
        .setStats(stats.snapshot())
        .setFeatureClass(cls)
        .build();
  }

  private static List<FeatureOuterClass.FeaturesUpdate> run(
      final FeaturesLoader loader, final FeatureOuterClass.FeaturesEvent... events)
      throws Exception {
    final var updates = new ArrayList<FeatureOuterClass.FeaturesUpdate>();
    new FeaturesUpdateGenerate(loader).reduce(List.of(events), new ListCollector<>(updates));
    return updates;
  }

  private static Map<String, FeatureOuterClass.Feature> features(
      final List<FeatureOuterClass.FeaturesUpdate> updates) {
    return updates.stream()
        .filter(FeatureOuterClass.FeaturesUpdate::hasFeature)
        .map(FeatureOuterClass.FeaturesUpdate::getFeature)
        .collect(Collectors.toMap(FeatureOuterClass.Feature::getKey, f -> f));
  }

  private static List<FeatureOuterClass.FeatureLog> logs(
      final List<FeatureOuterClass.FeaturesUpdate> updates) {
    return updates.stream()
        .filter(FeatureOuterClass.FeaturesUpdate::hasLog)
        .map(FeatureOuterClass.FeaturesUpdate::getLog)
        .collect(Collectors.toList());
  }

  @Test
  public void testDefaultConstructorTreatsEveryFeatureAsNew() throws Exception {
    final var updates = new ArrayList<FeatureOuterClass.FeaturesUpdate>();
    new FeaturesUpdateGenerate()
        .reduce(List.of(event("n", counterStats(0, 20))), new ListCollector<>(updates));

    assertEquals(1, logs(updates).size());
    assertTrue(logs(updates).get(0).hasFeatureCreate());
    assertEquals(COUNTER, features(updates).get("n").getFeatureClass());
  }

  @Test
  public void testLoaderIsAskedForTheEventSchema() throws Exception {
    final var requested = new ArrayList<String>();
    run(
        schemaId -> {
          requested.add(schemaId);
          return Map.of();
        },
        event("n", counterStats(0, 20)));

    assertEquals(List.of(SCHEMA_ID), requested);
  }

  @Test
  public void testNewFeaturesAreAddedNextToExistingOnes() throws Exception {
    final var existing = Map.of("n", stored("n", counterStats(0, 20), COUNTER));

    final var updates =
        run(
            schemaId -> existing,
            event("n", counterStats(20, 20)),
            event("id", stringStats("a", "b", "c")));

    final var logs = logs(updates);
    assertEquals(1, logs.size());
    assertTrue(logs.get(0).hasFeatureCreate());
    assertEquals("id", logs.get(0).getFeatureCreate().getKey());
    assertEquals(EPHEMERAL, logs.get(0).getFeatureCreate().getFeatureClass());
    assertEquals(Map.of("n", COUNTER, "id", EPHEMERAL), classes(features(updates)));
  }

  @Test
  public void testMergesNewStatsWithExistingStats() throws Exception {
    final var existing =
        Map.of(
            "n", stored("n", counterStats(0, 20), COUNTER),
            "id", stored("id", stringStats("a", "b"), EPHEMERAL));

    final var features =
        features(
            run(
                schemaId -> existing,
                event("n", counterStats(20, 30)),
                event("id", stringStats("b", "c"))));

    final var numeric = features.get("n").getStats().getIntStats();
    assertEquals(50, numeric.getValueStats().getCount());
    assertEquals(0.0, numeric.getValueStats().getMin(), 0);
    assertEquals(49.0, numeric.getValueStats().getMax(), 0);

    final var strings = features.get("id").getStats().getStringStats();
    assertEquals(4, strings.getCount());
    final var buckets = new HashMap<String, Long>();
    strings.getHistogram().getBucketsList().forEach(b -> buckets.put(b.getValue(), b.getCount()));
    assertEquals(Map.of("a", 1L, "b", 2L, "c", 1L), buckets);
  }

  @Test
  public void testUnchangedClassIsPreservedWithoutALog() throws Exception {
    final var existing = Map.of("n", stored("n", counterStats(0, 20), COUNTER));

    final var updates = run(schemaId -> existing, event("n", counterStats(20, 20)));

    assertEquals(List.of(), logs(updates));
    assertEquals(COUNTER, features(updates).get("n").getFeatureClass());
  }

  @Test
  public void testChangedClassIsDetected() throws Exception {
    // Stored as a gauge; the merged statistics now describe a counter.
    final var existing = Map.of("n", stored("n", counterStats(0, 20), GAUGE));

    final var updates = run(schemaId -> existing, event("n", counterStats(20, 20)));

    final var logs = logs(updates);
    assertEquals(1, logs.size());
    assertTrue(logs.get(0).hasFeatureUpdate());
    final var update = logs.get(0).getFeatureUpdate();
    assertEquals("n", update.getBefore().getKey());
    assertEquals(GAUGE, update.getBefore().getFeatureClass());
    assertEquals(COUNTER, update.getAfter().getFeatureClass());
    assertEquals(COUNTER, features(updates).get("n").getFeatureClass());
  }

  @Test
  public void testNoEventsProduceNoUpdatesAndNoLoad() throws Exception {
    final var updates =
        run(
            schemaId -> {
              throw new AssertionError("loader should not be called");
            });
    assertEquals(List.of(), updates);
  }

  @Test
  public void testFunctionIsSerializableWithDefaultAndMongoLoaders() throws Exception {
    serialize(new FeaturesUpdateGenerate());
    serialize(new FeaturesUpdateGenerate(new MongoFeaturesLoader("mongodb://unused", "test")));
  }

  @Test
  public void testLoadFromStoreTreatsMissingSchemaAsEmpty() throws Exception {
    final var store =
        new FakeStore(CompletableFuture.failedFuture(new MongoFindException("no result found")));
    assertEquals(Map.of(), FeaturesLoader.loadFrom(store, SCHEMA_ID, 1));
  }

  @Test
  public void testLoadFromStoreReturnsStoredFeatures() throws Exception {
    final var feature = stored("n", counterStats(0, 5), COUNTER);
    final var model =
        FeatureOuterClass.FeaturesModel.newBuilder()
            .setMetadata(METADATA)
            .putFeatures("n", feature)
            .build();
    final var store = new FakeStore(CompletableFuture.completedFuture(model));
    assertEquals(Map.of("n", feature), FeaturesLoader.loadFrom(store, SCHEMA_ID, 1));
  }

  private static Map<String, FeatureOuterClass.FeatureClass> classes(
      final Map<String, FeatureOuterClass.Feature> features) {
    final var classes = new HashMap<String, FeatureOuterClass.FeatureClass>();
    features.forEach((key, feature) -> classes.put(key, feature.getFeatureClass()));
    return classes;
  }

  private static void serialize(final Object object) throws Exception {
    try (final var out = new ObjectOutputStream(new ByteArrayOutputStream())) {
      out.writeObject(object);
    }
  }

  private static final class FakeStore implements FeaturesStore {
    private final CompletableFuture<FeatureOuterClass.FeaturesModel> features;

    FakeStore(final CompletableFuture<FeatureOuterClass.FeaturesModel> features) {
      this.features = features;
    }

    @Override
    public CompletableFuture<FeatureOuterClass.FeaturesModel> getFeatures(final String schemaId) {
      return features;
    }

    @Override
    public CompletableFuture<Collection<FeatureOuterClass.FeatureLogsModel>> getFeatureLogs(
        final String schemaId) {
      return CompletableFuture.completedFuture(List.of());
    }
  }
}
