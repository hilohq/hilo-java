package com.hilohq.hilo.statistics.stats;

public final class DefinedBuckets {
    private final double[] buckets;

    public DefinedBuckets(final double ...buckets) {
        this.buckets = buckets;
    }

    public double[] buckets() {
        return buckets;
    }
}
