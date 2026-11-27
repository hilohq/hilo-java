package com.hilohq.hilo.features.stats.numbers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class DoubleStatsMergeTest {

  private static DoubleStats observed(final int from, final int to) {
    final var stats = new DoubleStats();
    for (int i = from; i < to; i++) {
      stats.observe((double) i);
    }
    return stats;
  }

  @Test
  public void testMergeKeepsContinuityStatsOfBothSides() {
    final var left = observed(0, 10);
    final var right = observed(10, 30);
    final var leftWindows = left.snapshot().getContinuityStats().getCount();
    final var rightWindows = right.snapshot().getContinuityStats().getCount();

    left.merge(right);

    final var snapshot = left.snapshot();
    assertEquals(30, snapshot.getValueStats().getCount());
    assertEquals(leftWindows + rightWindows, snapshot.getContinuityStats().getCount());
  }

  @Test
  public void testMergeAfterSnapshotRoundTrip() {
    final var restored = DoubleStats.fromSnapshot(observed(0, 10).snapshot());
    final var other = observed(10, 20);
    final var expectedWindows =
        restored.snapshot().getContinuityStats().getCount()
            + other.snapshot().getContinuityStats().getCount();

    restored.merge(other);

    assertEquals(expectedWindows, restored.snapshot().getContinuityStats().getCount());
  }

  @Test
  public void testSnapshotOfASingleValue() {
    final var snapshot = observed(5, 6).snapshot();
    assertEquals(1, snapshot.getValueStats().getCount());
    assertEquals(5.0, snapshot.getValueStats().getMin(), 0);
    assertEquals(0.0, snapshot.getValueStats().getStandardDeviation(), 0);
  }
}
