package com.hilohq.hilo.features.stats.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import org.junit.Test;

public final class DiscreteHistogramTest {

  @Test
  public void testDefaultConstructorTracksValues() {
    final var histogram = new DiscreteHistogram<String>();
    histogram.observe("a");
    histogram.observe("a");
    histogram.observe("b", 3);

    assertTrue(histogram.keepsTrack());
    assertEquals(2, histogram.size());
    assertEquals(2, histogram.count("a"));
    assertEquals(3, histogram.count("b"));
  }

  @Test
  public void testConstructorHonoursKeepsTrack() {
    final var tracking = new DiscreteHistogram<String>(true, new HashMap<>());
    tracking.observe("a");
    assertTrue(tracking.keepsTrack());
    assertEquals(1, tracking.count("a"));

    final var notTracking = new DiscreteHistogram<String>(false, new HashMap<>());
    notTracking.observe("a");
    assertFalse(notTracking.keepsTrack());
    assertEquals(0, notTracking.size());
  }

  @Test
  public void testStopsTrackingAfterTooManyDistinctValues() {
    final var histogram = new DiscreteHistogram<Integer>();
    for (int i = 0; i <= 1024; i++) {
      histogram.observe(i);
    }

    assertFalse(histogram.keepsTrack());
    assertEquals(0, histogram.size());
  }

  @Test
  public void testMergeAddsCounts() {
    final var left = new DiscreteHistogram<String>();
    left.observe("a");
    final var right = new DiscreteHistogram<String>();
    right.observe("a");
    right.observe("b");

    left.merge(right);

    assertTrue(left.keepsTrack());
    assertEquals(2, left.count("a"));
    assertEquals(1, left.count("b"));
  }

  @Test
  public void testMergeWithNonTrackingHistogramStopsTracking() {
    final var left = new DiscreteHistogram<String>();
    left.observe("a");

    left.merge(new DiscreteHistogram<>(false, new HashMap<>()));

    assertFalse(left.keepsTrack());
    assertEquals(0, left.size());
  }
}
