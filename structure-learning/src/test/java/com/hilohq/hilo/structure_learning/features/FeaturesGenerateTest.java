package com.hilohq.hilo.structure_learning.features;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import com.hilohq.hilo.structure_learning.GoldenFeatures;
import com.hilohq.hilo.structure_learning.Samples;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Runs FeaturesStatsGenerate and FeaturesUpdateGenerate in-process on the sample datasets, the same
 * way JsonFeaturesGenerateStages chains them after schema validation.
 */
public final class FeaturesGenerateTest {

  private static List<FeatureOuterClass.FeaturesUpdate> generate(final String sample)
      throws Exception {
    return GoldenFeatures.updates(sample);
  }

  private static Map<String, FeatureOuterClass.Feature> expectedFeatures(final String sample) {
    return Samples.readProto(
            sample, "features.json", FeatureOuterClass.FeaturesModel.getDefaultInstance())
        .getFeaturesMap();
  }

  private static final List<String> BLOCKS_STRING_FIELDS =
      List.of(
          "extraData",
          "hash",
          "logsBloom",
          "miner",
          "mixHash",
          "nonce",
          "parentHash",
          "receiptsRoot",
          "sha3Uncles",
          "stateRoot",
          "transactionsRoot");

  private static final List<String> TRANSACTIONS_STRING_FIELDS =
      List.of("blockHash", "from", "hash", "input", "r", "s", "to", "v");

  private static Map<String, FeatureOuterClass.FeatureClass> featureClasses(
      final List<FeatureOuterClass.FeaturesUpdate> updates) {
    final var classes = new HashMap<String, FeatureOuterClass.FeatureClass>();
    for (final var update : updates) {
      if (update.hasFeature()) {
        classes.put(update.getFeature().getKey(), update.getFeature().getFeatureClass());
      }
    }
    return classes;
  }

  private static Map<String, FeatureOuterClass.FeatureClass> expectedFeatureClasses(
      final String sample) {
    final var expected =
        Samples.readProto(
            sample, "features.json", FeatureOuterClass.FeaturesModel.getDefaultInstance());
    final var classes = new HashMap<String, FeatureOuterClass.FeatureClass>();
    for (final var entry : expected.getFeaturesMap().entrySet()) {
      classes.put(entry.getKey(), entry.getValue().getFeatureClass());
    }
    return classes;
  }

  private static long logs(final List<FeatureOuterClass.FeaturesUpdate> updates) {
    return updates.stream().filter(FeatureOuterClass.FeaturesUpdate::hasLog).count();
  }

  @Test
  public void testClassifiesTransactionsSampleFeatures() throws Exception {
    final var updates = generate("transactions");
    final var classes = featureClasses(updates);

    assertEquals(expectedFeatureClasses("transactions"), classes);
    assertEquals(14, logs(updates));
    // gasPrice is a JSON string in the logs but is re-parsed as a number by StringParser
    assertTrue(classes.get("gasPrice").hasDiscrete());
    for (final var key : TRANSACTIONS_STRING_FIELDS) {
      assertTrue(key + " should be ephemeral", classes.get(key).hasEphemeral());
    }
  }

  @Test
  public void testClassifiesBlocksSampleFeatures() throws Exception {
    final var updates = generate("blocks");
    final var classes = featureClasses(updates);

    assertEquals(expectedFeatureClasses("blocks"), classes);
    assertEquals(18, logs(updates));
    assertTrue(classes.get("number").hasCounter());
    assertTrue(classes.get("totalDifficulty").hasMonotonic());
    assertTrue(classes.get("timestamp").hasTimestamp());
    for (final var key : BLOCKS_STRING_FIELDS) {
      assertTrue(key + " should be ephemeral", classes.get(key).hasEphemeral());
    }
  }

  @Test
  public void testGeneratedStatsMatchGoldenFiles() throws Exception {
    for (final var sample : List.of("blocks", "transactions")) {
      assertEquals(sample, expectedFeatures(sample), GoldenFeatures.model(sample).getFeaturesMap());
    }
  }

  @Test
  public void testStringFieldsAreTracked() throws Exception {
    final var features = GoldenFeatures.model("blocks").getFeaturesMap();
    for (final var key : BLOCKS_STRING_FIELDS) {
      final var stringStats = features.get(key).getStats().getStringStats();
      assertEquals(key, 100, stringStats.getCount());
      assertTrue(key, stringStats.getHistogram().getKeepsTrack());
      assertTrue(key, stringStats.getHistogram().getBucketsCount() > 0);
    }
    // every block has a distinct hash
    assertEquals(
        100, features.get("hash").getStats().getStringStats().getHistogram().getBucketsCount());
  }

  @Test
  public void testFeatureUpdatesCarryTheSchemaMetadata() throws Exception {
    for (final var update : generate("blocks")) {
      assertEquals(Samples.SCHEMA_ID, update.getMetadata().getSchemaId());
      assertEquals(Samples.PROJECT_ID, update.getMetadata().getProjectId());
      assertEquals(Samples.SOURCE_ID, update.getMetadata().getSourceId());
    }
  }
}
