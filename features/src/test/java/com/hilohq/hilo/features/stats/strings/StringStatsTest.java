package com.hilohq.hilo.features.stats.strings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public final class StringStatsTest {

  private static Map<String, Long> buckets(final FeatureOuterClass.StringStats snapshot) {
    final var buckets = new HashMap<String, Long>();
    for (final var bucket : snapshot.getHistogram().getBucketsList()) {
      buckets.put(bucket.getValue(), bucket.getCount());
    }
    return buckets;
  }

  @Test
  public void testTracksObservedStrings() {
    final var stats = new StringStats();
    stats.observe("0xabc");
    stats.observe("0xabc");
    stats.observe("0xdef");

    final var snapshot = stats.snapshot();

    assertEquals(3, snapshot.getCount());
    assertTrue(snapshot.getHistogram().getKeepsTrack());
    assertEquals(Map.of("0xabc", 2L, "0xdef", 1L), buckets(snapshot));
  }

  @Test
  public void testSnapshotRoundTripKeepsTracking() {
    final var stats = new StringStats();
    stats.observe("a");

    final var restored = StringStats.fromSnapshot(stats.snapshot());
    restored.observe("a");
    restored.observe("b");

    final var snapshot = restored.snapshot();
    assertEquals(3, snapshot.getCount());
    assertEquals(Map.of("a", 2L, "b", 1L), buckets(snapshot));
  }

  @Test
  public void testMerge() {
    final var left = new StringStats();
    left.observe("a");
    final var right = new StringStats();
    right.observe("a");
    right.observe("b");

    left.merge(right);

    final var snapshot = left.snapshot();
    assertEquals(3, snapshot.getCount());
    assertEquals(Map.of("a", 2L, "b", 1L), buckets(snapshot));
  }

  @Test
  public void testCountSurvivesHighCardinality() {
    final var stats = new StringStats();
    for (int i = 0; i < 2000; i++) {
      stats.observe("value-" + i);
    }

    final var snapshot = stats.snapshot();
    assertFalse(snapshot.getHistogram().getKeepsTrack());
    assertEquals(0, snapshot.getHistogram().getBucketsCount());
    assertEquals(2000, snapshot.getCount());
    assertEquals(2000, StringStats.fromSnapshot(snapshot).snapshot().getCount());
  }
}
