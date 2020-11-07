package com.hilohq.hilo.statistics.stats;

import static org.junit.Assert.assertEquals;

import java.util.*;
import org.junit.Test;

public final class NumericPercentileHistogramAccumulatorTest {

  @Test
  public void testAddSingleValue() {
    final var histogram = new NumericPercentileHistogramAccumulator();
    histogram.add((double) 1);

    final var buckets = new ArrayList<>(histogram.buckets());
    assertEquals(1, buckets.size());
    assertEquals(new Interval(1, 1), buckets.get(0).getKey());
    assertEquals(Long.valueOf(1), buckets.get(0).getValue());
  }

  @Test
  public void testAddMoreValuesThanMaxBucketsWithBalancedBuckets() {
    final var expectedBuckets = new ArrayList<Map.Entry<Interval, Long>>();
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(0.0, 116.0), 117L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(117.0, 262.0), 146L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(263.0, 353.0), 91L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(354.0, 475.0), 122L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(476.0, 548.0), 73L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(549.0, 638.0), 90L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(639.0, 760.0), 122L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(761.0, 850.0), 90L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(851.0, 998.0), 148L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(999.0, 999.0), 1L));

    final var boundaries =
        new NumericPercentileHistogramAccumulator.Boundaries(
            10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
    final var histogram = new NumericPercentileHistogramAccumulator(boundaries, 100);

    for (int i = 0; i < 1000; i++) {
      histogram.add((double) i);
    }
    histogram.mergeBuckets();
    final var buckets = new ArrayList<>(histogram.buckets());

    assertEquals(buckets.size(), expectedBuckets.size());
    assertEquals(buckets.stream().mapToLong(Map.Entry::getValue).sum(), 1000);
    for (int i = 0; i < buckets.size(); i++) {
      assertEquals(buckets.get(i).getKey(), expectedBuckets.get(i).getKey());
      assertEquals(buckets.get(i).getValue(), expectedBuckets.get(i).getValue());
    }
  }

  @Test
  public void testAddMoreValuesThanMaxBucketsWithDefaultBuckets() {
    final var expectedBuckets = new ArrayList<Map.Entry<Interval, Long>>();
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(0, 0), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(1, 1), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(2, 2), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(3, 3), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(4, 4), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(5, 5), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(6, 6), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(7, 14), 8L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(15, 53), 39L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(54, 102), 49L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(103, 151), 49L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(152, 200), 49L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(201, 299), 99L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(300, 398), 99L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(399, 497), 99L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(498, 596), 99L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(597, 695), 99L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(696, 843), 148L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(844, 892), 49L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(893, 941), 49L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(942, 992), 51L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(993, 993), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(994, 994), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(995, 995), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(996, 996), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(997, 997), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(998, 998), 1L));
    expectedBuckets.add(new AbstractMap.SimpleImmutableEntry<>(new Interval(999, 999), 1L));

    final var histogram = new NumericPercentileHistogramAccumulator();

    for (int i = 0; i < 1000; i++) {
      histogram.add((double) i);
    }
    histogram.mergeBuckets();

    final var buckets = new ArrayList<>(histogram.buckets());
    assertEquals(buckets.size(), expectedBuckets.size());
    assertEquals(1000, buckets.stream().mapToLong(Map.Entry::getValue).sum());
    for (int i = 0; i < buckets.size(); i++) {
      assertEquals(buckets.get(i).getKey(), expectedBuckets.get(i).getKey());
      assertEquals(buckets.get(i).getValue(), expectedBuckets.get(i).getValue());
    }
  }
}
