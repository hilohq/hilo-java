package com.hilohq.hilo.statistics.stats;

import org.junit.Test;

import java.util.ArrayList;


import static org.junit.Assert.assertEquals;

public final class LongHistogramTest {

    @Test
    public void testAddSingleValue() {
        final var histogram = new LongHistogram();
        histogram.add(1);

        final var buckets = new ArrayList<>(histogram.buckets());
        assertEquals(1, buckets.size());
        assertEquals(new LongInterval(1, 1), buckets.get(0).getKey());
        assertEquals(Long.valueOf(1), buckets.get(0).getValue());
    }

    @Test
    public void testAddMoreValuesThanMaxBuckets() {
        final var definedBuckets = new DefinedBuckets(
                10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
        final var histogram = new LongHistogram(definedBuckets, 20);

        for (int i = 0; i < 100; i++) {
            histogram.add(i);
        }

        final var buckets = new ArrayList<>(histogram.buckets());
        for (final var bucket : buckets) {
            System.out.println(bucket.getKey().left() + " " + bucket.getKey().right() + " " + bucket.getValue());
        }

        assertEquals(buckets.size(), 10);

        for (int i = 0; i < buckets.size(); i++) {
            final var bucket = buckets.get(i);

            assertEquals(i*10, bucket.getKey().left());
            assertEquals((i + 1)*10, bucket.getKey().left());
            assertEquals(Long.valueOf(10), bucket.getValue());
        }
    }
}
