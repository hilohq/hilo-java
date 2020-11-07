package com.hilohq.hilo.statistics.stats;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Integer.max;

public final class LongHistogram {
    private static final int DEFAULT_MAX_BUCKETS = 1000;
    private final DefinedBuckets definedBuckets;
    private final TreeMap<LongInterval, Long> buckets;
    private final int maxBuckets;

    public LongHistogram() {
        this(new DefinedBuckets(
                0.0000001, 0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 1,
                5, 10, 15,
                20, 30, 40, 50, 60, 70, 80,
                85, 90, 95,
                99, 99.9, 99.99, 99.999, 99.9999, 99.99999, 99.999999));
    }

    public LongHistogram(final DefinedBuckets definedBuckets) {
        this(definedBuckets, DEFAULT_MAX_BUCKETS);
    }

    public LongHistogram(final DefinedBuckets definedBuckets,
                         final int maxBuckets) {
        this.buckets = new TreeMap<>();
        this.definedBuckets = definedBuckets;
        this.maxBuckets = max(definedBuckets.buckets().length, maxBuckets);
    }

    public Collection<Map.Entry<LongInterval, Long>> buckets() {
        return buckets.entrySet();
    }

    private void mergeBuckets() {
        System.out.println("before merge buckets " + buckets.size());
        for (final var bucket : buckets.entrySet()) {
            System.out.println(bucket.getKey().left() + " " + bucket.getKey().right() + " " + bucket.getValue());
        }
        final var totalCount = (Double) buckets.values().stream().mapToDouble(Long::doubleValue).sum();
        var previousBucketThreshold = 0D;
        var lastBucketEntry = buckets.firstEntry();

        for (final var currentBucketThreshold : definedBuckets.buckets()) {
            final var threshold = currentBucketThreshold - previousBucketThreshold;
            previousBucketThreshold = currentBucketThreshold;

            if (lastBucketEntry == null) {
                break;
            }

            var newBucket = lastBucketEntry.getKey();
            var newBucketCount = lastBucketEntry.getValue();
            System.out.println("RATIO " + (newBucketCount + lastBucketEntry.getValue()) * 100 / (double) totalCount);
            for (
                    var currentBucketEntry = lastBucketEntry;
                    currentBucketEntry != null && (newBucketCount + currentBucketEntry.getValue()) * 100 / (double) totalCount < threshold;
                    currentBucketEntry = buckets.higherEntry(currentBucketEntry.getKey()),
                            lastBucketEntry = currentBucketEntry
            ) {
                System.out.println("RATIO " + (newBucketCount + currentBucketEntry.getValue()) / (double) totalCount);
                newBucket = newBucket.merge(currentBucketEntry.getKey());
                buckets.remove(currentBucketEntry.getKey());
            }

            System.out.println("PUT BUCKET " + newBucket.left() + " " + newBucket.right() + " " + newBucketCount);
            buckets.put(newBucket, newBucketCount);
        }

        System.out.println();
        System.out.println("after merge buckets");
        for (final var bucket : buckets.entrySet()) {
            System.out.println(bucket.getKey().left() + " " + bucket.getKey().right() + " " + bucket.getValue());
        }
        System.out.println();
    }

    public void add(final long value) {
        final var current = new LongInterval(value);
        final var lowerEntry = buckets.lowerEntry(current);
        if (lowerEntry == null) {
            if (buckets.size() >= maxBuckets) {
                mergeBuckets();
            }

            buckets.put(current, 1L);
            return;
        }

        if (lowerEntry.getKey().right() > value) {
            buckets.put(lowerEntry.getKey(), lowerEntry.getValue() + 1);
        } else {
            if (buckets.size() >= maxBuckets) {
                mergeBuckets();
            }

            buckets.put(current, 1L);
        }
    }
}
