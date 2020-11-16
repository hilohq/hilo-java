package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.features.stats.numbers.IntegerUtils;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.ArrayList;
import java.util.Arrays;

public final class FeatureInfer {
  private long getStringCount(final FeatureOuterClass.FeatureStats stats) {
    if (stats.hasStringStats()) {
      return stats.getStringStats().getCount();
    } else {
      return 0;
    }
  }

  private long getIntCount(final FeatureOuterClass.FeatureStats stats) {
    if (stats.hasIntStats()) {
      return stats.getIntStats().getCount();
    } else {
      return 0;
    }
  }

  private long getLongCount(final FeatureOuterClass.FeatureStats stats) {
    if (stats.hasLongStats()) {
      return stats.getLongStats().getCount();
    } else {
      return 0;
    }
  }

  private long getFloatCount(final FeatureOuterClass.FeatureStats stats) {
    if (stats.hasFloatStats()) {
      return stats.getFloatStats().getCount();
    } else {
      return 0;
    }
  }

  private long getDoubleCount(final FeatureOuterClass.FeatureStats stats) {
    if (stats.hasDoubleStats()) {
      return stats.getDoubleStats().getCount();
    } else {
      return 0;
    }
  }

  private long getBooleanCount(final FeatureOuterClass.FeatureStats stats) {
    if (stats.hasBooleanStats()) {
      return stats.getBooleanStats().getCount();
    } else {
      return 0;
    }
  }

  private long getBytesCount(final FeatureOuterClass.FeatureStats stats) {
    if (stats.hasBytesStats()) {
      return stats.getBytesStats().getCount();
    } else {
      return 0;
    }
  }

  private FeatureOuterClass.FeatureClass inferInt(final FeatureOuterClass.NumericStats snapshot) {
    return inferLong(snapshot);
  }

  private FeatureOuterClass.FeatureClass inferLong(final FeatureOuterClass.NumericStats snapshot) {
    final var histogram = snapshot.getValueHistogram();
    final var values = new ArrayList<Long>();
    for (final var bucket : histogram.getBucketsList()) {
      values.add((long) bucket.getMin());
      values.add((long) bucket.getMax());
    }

    final var probability = IntegerUtils.isTimestampWithProbability(values);
    if (probability < 0.8) {
      return inferNumeric(snapshot);

    } else {
      return FeatureOuterClass.FeatureClass.newBuilder()
          .setTimestamp(FeatureOuterClass.FeatureClass.Timestamp.newBuilder())
          .build();
    }
  }

  private FeatureOuterClass.FeatureClass inferFloat(final FeatureOuterClass.NumericStats snapshot) {
    return inferNumeric(snapshot);
  }

  private FeatureOuterClass.FeatureClass inferDouble(
      final FeatureOuterClass.NumericStats snapshot) {
    return inferNumeric(snapshot);
  }

  private FeatureOuterClass.FeatureClass inferNumeric(
      final FeatureOuterClass.NumericStats snapshot) {
    final var diffHistogram = snapshot.getDifferentialHistogram();

    if (diffHistogram.getBoundariesCount() == 0) {
      return FeatureOuterClass.FeatureClass.newBuilder()
          .setGauge(FeatureOuterClass.FeatureClass.Gauge.newBuilder())
          .build();
    }

    final var bucket = diffHistogram.getBuckets(0);
    if (bucket.getMax() > 0) {
      return FeatureOuterClass.FeatureClass.newBuilder()
          .setMonotonic(FeatureOuterClass.FeatureClass.Monotonic.newBuilder())
          .build();
    } else {
      return FeatureOuterClass.FeatureClass.newBuilder()
          .setGauge(FeatureOuterClass.FeatureClass.Gauge.newBuilder())
          .build();
    }
  }

  public FeatureOuterClass.FeatureClass infer(final FeatureStats stats) {
    final var snapshot = stats.snapshot();

    final var booleanCount = getBooleanCount(snapshot);
    final var intCount = getIntCount(snapshot);
    final var longCount = getLongCount(snapshot);
    final var floatCount = getFloatCount(snapshot);
    final var doubleCount = getDoubleCount(snapshot);
    final var stringCount = getStringCount(snapshot);
    final var bytesCount = getBytesCount(snapshot);

    final var array =
        Arrays.asList(
            booleanCount, intCount, longCount, floatCount, doubleCount, stringCount, bytesCount);

    int maxAt = 0;
    for (int i = 0; i < array.size(); i++) {
      maxAt = array.get(i) > array.get(maxAt) ? i : maxAt;
    }

    switch (maxAt) {
      case 0:
        return FeatureOuterClass.FeatureClass.newBuilder()
            .setBoolEnum(FeatureOuterClass.FeatureClass.BooleanEnum.newBuilder())
            .build();
      case 1:
        return inferInt(snapshot.getIntStats());
      case 2:
        return inferLong(snapshot.getLongStats());
      case 3:
        return inferFloat(snapshot.getFloatStats());
      case 4:
        return inferDouble(snapshot.getDoubleStats());
      case 5:
      case 6:
        return FeatureOuterClass.FeatureClass.newBuilder()
            .setEphemeral(FeatureOuterClass.FeatureClass.Ephemeral.newBuilder())
            .build();
      default:
        throw new IllegalStateException("unexpected index " + maxAt);
    }
  }
}
