package com.hilohq.hilo.statistics.stats;

public final class LongInterval implements Comparable<LongInterval> {

    private final long left;
    private final long right;

    public LongInterval(final long left, final long right) {
        if (left > right) {
            throw new IllegalArgumentException("left must be smaller than right");
        }

        this.left = left;
        this.right = right;
    }

    public LongInterval(final long value) {
        this(value, value);
    }

    public long left() {
        return left;
    }

    public long right() {
        return right;
    }

    public LongInterval merge(final LongInterval other) {
        return new LongInterval(
                Math.min(left, other.left()),
                Math.max(right, other.right())
        );
    }

    public int hashCode() {
        return (int) ((left * 31 + right) % 17);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }

        if (!other.getClass().equals(LongInterval.class)) {
            return false;
        }

        final var otherInterval = (LongInterval) other;
        return otherInterval.right() == right && otherInterval.left() == left;
    }

    @Override
    public int compareTo(final LongInterval other) {
        if (other.left() < left || (other.left() == left && other.right() < right)) {
            return 1;

        } else if (other.left() > left || (other.left() == left && other.right() > right)) {
            return -1;

        } else {
            return 0;
        }
    }
}
