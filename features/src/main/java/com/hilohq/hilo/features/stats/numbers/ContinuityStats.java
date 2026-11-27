package com.hilohq.hilo.features.stats.numbers;

import com.google.common.math.StatsAccumulator;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class ContinuityStats {
  private static final int DEFAULT_WINDOW_SIZE = 16;

  private final StatsAccumulator statsAccumulator;
  private final Window window;

  public ContinuityStats() {
    this(new StatsAccumulator());
  }

  private ContinuityStats(final StatsAccumulator statsAccumulator) {
    this.statsAccumulator = statsAccumulator;
    this.window = new Window(DEFAULT_WINDOW_SIZE);
  }

  public void observe(final Double value) {
    window.observe(value);
    if (window.size() > 2) {
      final var score = calculateContinuityScore(window);
      statsAccumulator.add(score);
    }
  }

  /**
   * Adds the window scores collected by {@code other}. Windows are not carried across, so scores
   * spanning the boundary between the two value sequences are not computed.
   */
  public void merge(final ContinuityStats other) {
    statsAccumulator.addAll(other.statsAccumulator);
  }

  private static double calculateContinuityScoreForArray(final List<Double> diffs) {
    if (diffs.isEmpty()) {
      return 0;
    }

    var sum = 0;
    var previous = diffs.get(0);
    for (int i = 1; i < diffs.size(); i++) {
      final var current = diffs.get(i);
      if (previous > current) {
        sum -= 1;
      } else {
        sum += 1;
      }
      previous = current;
    }

    return (sum - diffs.size()) / (double) (-2 * diffs.size());
  }

  private static double calculateContinuityScore(final Window window) {
    if (window.size() < 2) {
      return 0;
    }

    final var index = window.size() / 2;
    final var beforeDiffs = new ArrayList<Double>(window.size() / 2);
    final var afterDiffs = new ArrayList<Double>(window.size() / 2);

    for (int i = 0; i < index; i++) {
      beforeDiffs.add(Math.abs(window.get(i) - window.get(index)));
    }

    for (int i = window.size() - 1; i > index; i--) {
      afterDiffs.add(Math.abs(window.get(i) - window.get(index)));
    }

    final var beforeScore = calculateContinuityScoreForArray(beforeDiffs);
    final var afterScore = calculateContinuityScoreForArray(afterDiffs);

    return (beforeScore + afterScore) / 2;
  }

  public FeatureOuterClass.NumericStats.ValueStats snapshot() {
    final var builder =
        FeatureOuterClass.NumericStats.ValueStats.newBuilder()
            .setCount(statsAccumulator.count())
            .setBase64StatsRepr(
                Base64.getEncoder().encodeToString(statsAccumulator.snapshot().toByteArray()));
    if (statsAccumulator.count() > 0) {
      builder
          .setMean(statsAccumulator.mean())
          .setMax(statsAccumulator.max())
          .setMin(statsAccumulator.min());
    }

    if (statsAccumulator.count() > 1) {
      final var continuityScore =
          Math.min(statsAccumulator.mean() + statsAccumulator.sampleStandardDeviation(), 1);
      builder
          .setStandardDeviation(statsAccumulator.sampleStandardDeviation())
          .setContinuityScore(continuityScore);
    }
    return builder.build();
  }

  public static ContinuityStats fromSnapshot(
      final FeatureOuterClass.NumericStats.ValueStats snapshot) {
    final var valueStatsAccumulator = new com.google.common.math.StatsAccumulator();
    final var valueStats =
        com.google.common.math.Stats.fromByteArray(
            Base64.getDecoder().decode(snapshot.getBase64StatsRepr()));
    valueStatsAccumulator.addAll(valueStats);
    return new ContinuityStats(valueStatsAccumulator);
  }
}
