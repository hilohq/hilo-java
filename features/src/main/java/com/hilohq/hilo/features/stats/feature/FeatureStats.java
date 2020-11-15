package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.features.stats.booleans.BooleanStats;
import com.hilohq.hilo.features.stats.numbers.DoubleStats;
import com.hilohq.hilo.features.stats.numbers.FloatStats;
import com.hilohq.hilo.features.stats.numbers.IntStats;
import com.hilohq.hilo.features.stats.numbers.LongStats;
import com.hilohq.hilo.features.stats.strings.BytesStats;
import com.hilohq.hilo.features.stats.strings.StringStats;
import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.parsers.value.ValueType;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.HashMap;
import java.util.Map;

public final class FeatureStats
    implements StatsCollector, Snapshot<FeatureOuterClass.FeatureStats> {
  private Map<ValueType, StatsCollector> stats;

  private FeatureStats(final Map<ValueType, StatsCollector> stats) {
    this.stats = stats;
  }

  public FeatureStats() {
    this(new HashMap<>());
  }

  private void addNewStatsCollectorForType(final ValueType type) {
    switch (type) {
      case Boolean:
        stats.put(type, new BooleanStats());
        break;
      case Int:
        stats.put(type, new IntStats());
        break;
      case Long:
        stats.put(type, new LongStats());
        break;
      case Float:
        stats.put(type, new FloatStats());
        break;
      case Double:
        stats.put(type, new DoubleStats());
        break;
      case String:
        stats.put(type, new StringStats());
        break;
      case Bytes:
        stats.put(type, new BytesStats());
        break;
      default:
        throw new IllegalArgumentException("unknown feature stats type " + type);
    }
  }

  public void observe(final Value value) {
    if (!stats.containsKey(value.type())) {
      addNewStatsCollectorForType(value.type());
    }

    stats.get(value.type()).observe(value);
  }

  @Override
  public void merge(final StatsCollector collector) {
    if (!collector.getClass().equals(FeatureStats.class)) {
      throw new IllegalArgumentException(
          "FeatureStats expects another " + "instance of FeatureStats for a merge");
    }

    final var other = (FeatureStats) collector;
    for (final var entry : other.stats.entrySet()) {
      if (stats.containsKey(entry.getKey())) {
        stats.get(entry.getKey()).merge(entry.getValue());
      } else {
        stats.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public static FeatureStats fromProtobuf(final FeatureOuterClass.FeatureStats snapshot) {
    final var stats = new HashMap<ValueType, StatsCollector>();
    if (snapshot.hasBooleanStats()) {
      stats.put(ValueType.Boolean, BooleanStats.fromSnapshot(snapshot.getBooleanStats()));
    }
    if (snapshot.hasIntStats()) {
      stats.put(ValueType.Int, IntStats.fromSnapshot(snapshot.getIntStats()));
    }
    if (snapshot.hasLongStats()) {
      stats.put(ValueType.Long, LongStats.fromSnapshot(snapshot.getLongStats()));
    }
    if (snapshot.hasFloatStats()) {
      stats.put(ValueType.Float, FloatStats.fromSnapshot(snapshot.getFloatStats()));
    }
    if (snapshot.hasDoubleStats()) {
      stats.put(ValueType.Double, DoubleStats.fromSnapshot(snapshot.getDoubleStats()));
    }
    if (snapshot.hasStringStats()) {
      stats.put(ValueType.String, StringStats.fromSnapshot(snapshot.getStringStats()));
    }
    if (snapshot.hasBytesStats()) {
      stats.put(ValueType.Bytes, BytesStats.fromSnapshot(snapshot.getBytesStats()));
    }

    return new FeatureStats(stats);
  }

  @Override
  public FeatureOuterClass.FeatureStats snapshot() {
    final var featureStatsBuilder = FeatureOuterClass.FeatureStats.newBuilder();
    for (final var entry : stats.entrySet()) {
      switch (entry.getKey()) {
        case Boolean:
          featureStatsBuilder.setBooleanStats(((BooleanStats) entry.getValue()).snapshot());
          break;
        case Int:
          featureStatsBuilder.setIntStats(((IntStats) entry.getValue()).snapshot());
          break;
        case Long:
          featureStatsBuilder.setLongStats(((LongStats) entry.getValue()).snapshot());
          break;
        case Float:
          featureStatsBuilder.setFloatStats(((FloatStats) entry.getValue()).snapshot());
          break;
        case Double:
          featureStatsBuilder.setDoubleStats(((DoubleStats) entry.getValue()).snapshot());
          break;
        case String:
          featureStatsBuilder.setStringStats(((StringStats) entry.getValue()).snapshot());
          break;
        case Bytes:
          featureStatsBuilder.setBytesStats(((BytesStats) entry.getValue()).snapshot());
          break;
        default:
          throw new IllegalArgumentException("unknown feature stats type " + entry.getValue());
      }
    }

    return featureStatsBuilder.build();
  }
}
