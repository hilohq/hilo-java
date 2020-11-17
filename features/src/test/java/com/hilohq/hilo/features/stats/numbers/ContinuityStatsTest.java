package com.hilohq.hilo.features.stats.numbers;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class ContinuityStatsTest {

  @Test
  public void testObserveContinuousTimestamp() {
    final var values = TestSamples.timestamps();

    final var stats = new ContinuityStats();
    for (final var value : values) {
      stats.observe(value);
    }

    final var snapshot = stats.snapshot();
    assertEquals(98, snapshot.getCount());
    assertTrue(Math.abs(0.5 - snapshot.getMin()) < 0.01);
    assertTrue(Math.abs(0.93 - snapshot.getMax()) < 0.1);
    assertTrue(Math.abs(0.91 - snapshot.getMean()) < 0.1);
    assertTrue(Math.abs(0.05 - snapshot.getStandardDeviation()) < 0.1);
  }

  @Test
  public void testObserveDiscreteSizes() {
    final var values = TestSamples.sizes();

    final var stats = new ContinuityStats();
    for (final var value : values) {
      stats.observe(value);
    }

    final var snapshot = stats.snapshot();
    assertEquals(98, snapshot.getCount());
    assertTrue(Math.abs(0.25 - snapshot.getMin()) < 0.01);
    assertTrue(Math.abs(0.72 - snapshot.getMax()) < 0.1);
    assertTrue(Math.abs(0.46 - snapshot.getMean()) < 0.1);
    assertTrue(Math.abs(0.08 - snapshot.getStandardDeviation()) < 0.1);
  }
}
