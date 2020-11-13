package com.hilohq.hilo.features.stats.numbers;

public final class Interval implements Comparable<Interval> {

  private static final double THRESHOLD = 0.0000000001;
  private final double min;
  private final double max;

  public Interval(final double min, final double max) {
    if (min > max) {
      throw new IllegalArgumentException("min must be smaller than max");
    }

    this.min = min;
    this.max = max;
  }

  public Interval(final double value) {
    this(value, value);
  }

  public double min() {
    return min;
  }

  public double max() {
    return max;
  }

  public Interval merge(final Interval other) {
    return new Interval(Math.min(min, other.min()), Math.max(max, other.max()));
  }

  public int hashCode() {
    return (int) ((min * 31 + max) % 17);
  }

  private static boolean equalsDouble(final double a, final double b) {
    return Math.abs(a - b) < THRESHOLD;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null) {
      return false;
    }

    if (!other.getClass().equals(Interval.class)) {
      return false;
    }

    final var otherInterval = (Interval) other;
    return equalsDouble(otherInterval.max(), max) && equalsDouble(otherInterval.min(), min);
  }

  @Override
  public int compareTo(final Interval other) {
    if (other.min() < min || (equalsDouble(other.min(), min) && other.max() < max)) {
      return 1;

    } else if (other.min() > min || (equalsDouble(other.min(), min) && other.max() > max)) {
      return -1;

    } else {
      return 0;
    }
  }
}
