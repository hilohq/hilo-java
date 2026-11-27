package com.hilohq.hilo.features.stats.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.hilohq.hilo.parsers.value.BooleanValue;
import com.hilohq.hilo.parsers.value.IntValue;
import com.hilohq.hilo.parsers.value.StringValue;
import org.junit.Test;

public final class FeatureInferTest {

  private final FeatureInfer infer = new FeatureInfer();

  @Test
  public void testStringFieldIsTrackedAndClassifiedEphemeral() {
    final var stats = new FeatureStats();
    for (int i = 0; i < 50; i++) {
      stats.observe(new StringValue("0x" + Integer.toHexString(i)));
    }

    final var snapshot = stats.snapshot();
    assertEquals(50, snapshot.getStringStats().getCount());
    assertEquals(50, snapshot.getStringStats().getHistogram().getBucketsCount());
    assertTrue(infer.infer(stats).hasEphemeral());
  }

  @Test
  public void testStringFieldSurvivesSnapshotAndMerge() {
    final var first = new FeatureStats();
    first.observe(new StringValue("a"));
    final var second = new FeatureStats();
    second.observe(new StringValue("b"));
    second.observe(new StringValue("b"));

    final var merged = FeatureStats.fromProtobuf(first.snapshot());
    merged.merge(FeatureStats.fromProtobuf(second.snapshot()));

    assertEquals(3, merged.snapshot().getStringStats().getCount());
    assertTrue(infer.infer(merged).hasEphemeral());
  }

  @Test
  public void testMostlyStringFieldIsNotClassifiedByItsMinorityType() {
    final var stats = new FeatureStats();
    for (int i = 0; i < 10; i++) {
      stats.observe(new StringValue("s" + i));
    }
    stats.observe(new IntValue(1));

    assertTrue(infer.infer(stats).hasEphemeral());
  }

  @Test
  public void testBooleanFieldIsClassifiedBoolEnum() {
    final var stats = new FeatureStats();
    stats.observe(new BooleanValue(true));
    stats.observe(new BooleanValue(false));
    stats.observe(new BooleanValue(true));

    assertEquals(3, stats.snapshot().getBooleanStats().getCount());
    assertTrue(infer.infer(stats).hasBoolEnum());
  }
}
