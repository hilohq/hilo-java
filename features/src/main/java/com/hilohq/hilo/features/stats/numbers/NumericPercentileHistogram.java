package com.hilohq.hilo.features.stats.numbers;

import static com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass.NumericStats.*;
import static java.lang.Integer.max;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class NumericPercentileHistogram {
  private static final int DEFAULT_MAX_BUCKETS = 1000;
  private final Boundaries boundaries;
  private final TreeMap<Interval, Long> buckets;
  private final int maxBuckets;

  private static class BucketFromEntryMapper
      implements Function<Map.Entry<Interval, Long>, PercentileHistogram.Bucket> {

    private final Boundaries boundaries;
    private int counter;

    BucketFromEntryMapper(final Boundaries boundaries) {
      this.boundaries = boundaries;
      this.counter = 0;
    }

    @Override
    public PercentileHistogram.Bucket apply(final Map.Entry<Interval, Long> entry) {
      final var boundary = boundaries.boundaries().get(counter);
      counter++;
      return PercentileHistogram.Bucket.newBuilder()
          .setCount(entry.getValue())
          .setMax(entry.getKey().max())
          .setMin(entry.getKey().min())
          .setBoundary(boundary)
          .build();
    }
  }

  public static NumericPercentileHistogram fromSnapshot(final PercentileHistogram snapshot) {
    final var buckets = new TreeMap<Interval, Long>();
    final var boundaries = snapshot.getBoundariesList();

    for (final var bucket : snapshot.getBucketsList()) {
      buckets.put(new Interval(bucket.getMin(), bucket.getMax()), bucket.getCount());
    }

    return new NumericPercentileHistogram(buckets, new Boundaries(boundaries), DEFAULT_MAX_BUCKETS);
  }

  public PercentileHistogram snapshot() {
    if (maxBuckets > buckets.size()) {
      mergeBuckets();
    }

    final var histogramBuckets =
        buckets
            .entrySet()
            .stream()
            .map(new BucketFromEntryMapper(boundaries))
            .collect(Collectors.toList());

    return PercentileHistogram.newBuilder()
        .addAllBuckets(histogramBuckets)
        .addAllBoundaries(boundaries.boundaries())
        .build();
  }

  public static class Boundaries {
    private final List<Double> boundaries;

    private Boundaries(final List<Double> boundaries) {
      if (boundaries.size() <= 0) {
        throw new IllegalArgumentException("boundaries must have at least one element");
      }

      double previous = boundaries.get(0);
      if (previous <= 0 || previous > 100) {
        throw new IllegalArgumentException(
            "boundaries must be a monotonically increasing "
                + "sequence of values between 0 and 100");
      }

      for (int i = 1; i < boundaries.size(); i++) {
        final var current = boundaries.get(i);
        if (current < previous || current > 100) {
          throw new IllegalArgumentException(
              "boundaries must be a monotonically increasing "
                  + "sequence of values between 0 and 100");
        }

        previous = current;
      }

      this.boundaries = boundaries;
    }

    private static List<Double> fromArray(final double... boundaries) {
      final var boundaryList = new ArrayList<Double>();
      for (final var boundary : boundaries) {
        boundaryList.add(boundary);
      }
      return boundaryList;
    }

    public Boundaries(final double... boundaries) {
      this(fromArray(boundaries));
    }

    public List<Double> boundaries() {
      return boundaries;
    }
  }

  public NumericPercentileHistogram() {
    this(
        new Boundaries(
            0.0000001, 0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 1, 5, 10, 15, 20, 30, 40, 50,
            60, 70, 80, 85, 90, 95, 99, 99.9, 99.99, 99.999, 99.9999, 99.99999, 99.999999));
  }

  public NumericPercentileHistogram(final Boundaries boundaries) {
    this(boundaries, DEFAULT_MAX_BUCKETS);
  }

  private NumericPercentileHistogram(
      final TreeMap<Interval, Long> buckets, final Boundaries boundaries, final int maxBuckets) {
    this.buckets = buckets;
    this.boundaries = boundaries;
    this.maxBuckets = max(boundaries.boundaries().size(), maxBuckets);
  }

  public NumericPercentileHistogram(final Boundaries boundaries, final int maxBuckets) {
    this(new TreeMap<>(), boundaries, maxBuckets);
  }

  public Collection<Map.Entry<Interval, Long>> buckets() {
    return buckets.entrySet();
  }

  public void mergeBuckets(final double thresholdRatio) {
    final var totalCount = (Double) buckets.values().stream().mapToDouble(Long::doubleValue).sum();
    var previousBucketThreshold = 0D;
    var lastBucketEntry = buckets.firstEntry();

    for (final var currentBucketThreshold : boundaries.boundaries()) {
      final var threshold = currentBucketThreshold - previousBucketThreshold;
      previousBucketThreshold = currentBucketThreshold;

      if (lastBucketEntry == null) {
        break;
      }

      var newBucket = lastBucketEntry.getKey();
      var newBucketCount = lastBucketEntry.getValue();
      buckets.remove(lastBucketEntry.getKey());
      for (var currentBucketEntry = buckets.higherEntry(lastBucketEntry.getKey());
          currentBucketEntry != null
              && (newBucketCount + currentBucketEntry.getValue()) * 100 / totalCount
                  < thresholdRatio * threshold
              && buckets.size() >= boundaries.boundaries().size();
          currentBucketEntry = buckets.higherEntry(currentBucketEntry.getKey()),
              lastBucketEntry = currentBucketEntry) {
        newBucket = newBucket.merge(currentBucketEntry.getKey());
        newBucketCount += currentBucketEntry.getValue();
        buckets.remove(lastBucketEntry.getKey());
        buckets.remove(currentBucketEntry.getKey());
      }

      buckets.put(newBucket, newBucketCount);
      lastBucketEntry = buckets.higherEntry(newBucket);
    }

    if (buckets.size() > boundaries.boundaries().size()) {
      if (thresholdRatio < 2) {
        mergeBuckets(thresholdRatio + 0.5);
        return;
      }

      throw new IllegalStateException("Should not attempt to increase thresholdRatio over 2");
    }
  }

  public void addBucket(final Interval interval, final long count) {
    final var lowerInterval = buckets.lowerKey(interval);
    final var higherInterval = buckets.higherKey(interval);
    final var noIntersection =
        (higherInterval == null && lowerInterval == null)
            || (higherInterval == null && lowerInterval.max() < interval.min())
            || (lowerInterval == null && higherInterval.min() > interval.max())
            || lowerInterval != null
                && higherInterval != null
                && lowerInterval.max() < interval.min()
                && higherInterval.min() > interval.max();
    final var intersectsLowerOnly =
        (higherInterval == null || higherInterval.min() > interval.max())
            && lowerInterval != null
            && lowerInterval.max() > interval.min();
    final var intersectsHigherOnly =
        (lowerInterval == null || lowerInterval.max() < interval.min())
            && higherInterval != null
            && higherInterval.min() < interval.max();
    final var intersectsHigherAndLower =
        higherInterval != null
            && lowerInterval != null
            && lowerInterval.max() > interval.min()
            && higherInterval.min() < interval.max();

    if (intersectsLowerOnly) {
      final var lowerCount = buckets.remove(lowerInterval);
      buckets.put(interval.merge(lowerInterval), count + lowerCount);

    } else if (intersectsHigherOnly) {
      final var higherCount = buckets.remove(higherInterval);
      buckets.put(interval.merge(higherInterval), count + higherCount);

    } else if (intersectsHigherAndLower) {
      final var lowerCount = buckets.remove(lowerInterval);
      final var higherCount = buckets.remove(higherInterval);
      buckets.put(
          interval.merge(lowerInterval).merge(higherInterval), lowerCount + higherCount + count);

    } else {
      buckets.put(interval, count);
    }
  }

  public void merge(final NumericPercentileHistogram other) {
    for (final var bucket : other.buckets.entrySet()) {
      addBucket(bucket.getKey(), bucket.getValue());
    }
  }

  public void mergeBuckets() {
    mergeBuckets(1);
  }

  public void observe(final Double value) {
    final var current = new Interval(value);
    final var lowerEntry = buckets.lowerEntry(current);
    if (lowerEntry == null) {
      if (buckets.size() >= maxBuckets) {
        mergeBuckets();
      }

      buckets.put(current, 1L);
      return;
    }

    if (lowerEntry.getKey().max() > value) {
      buckets.put(lowerEntry.getKey(), lowerEntry.getValue() + 1);
    } else {
      if (buckets.size() >= maxBuckets) {
        mergeBuckets();
      }

      buckets.put(current, 1L);
    }
  }
}
