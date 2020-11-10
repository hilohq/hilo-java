package com.hilohq.hilo.structure_learning.parsing.fields;

import com.hilohq.hilo.protobuf.stats.Stats;
import com.hilohq.hilo.statistics.stats.BooleanStatsAccumulator;
import com.hilohq.hilo.statistics.stats.NumericStatsAccumulator;
import com.hilohq.hilo.statistics.stats.StringStatsAccumulator;
import com.hilohq.hilo.structure_learning.parsing.schema.Samples;
import com.hilohq.hilo.structure_learning.parsing.value.Value;
import com.hilohq.hilo.structure_learning.parsing.value.ValueType;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public final class FieldStatsGenerate extends ProcessFunction<Samples, Stats.StatsEvent> {

  private static void initStatsForField(
      final FieldGroupStats stats, final String key, final ValueType type) {
    switch (type) {
      case Bool:
        stats.stats().put(key, new BooleanStatsAccumulator());
        break;
      case Int:
      case Long:
      case Float:
      case Double:
        stats.stats().put(key, new NumericStatsAccumulator());
        break;
      case String:
        stats.stats().put(key, new StringStatsAccumulator());
        break;
      default:
        throw new IllegalArgumentException("Unknown value type " + type);
    }
  }

  private static void addValue(final FieldGroupStats stats, final String key, final Value value) {
    final var acc = stats.stats().get(key);
    switch (value.type()) {
      case Bool:
        if (acc.getClass().equals(BooleanStatsAccumulator.class)) {
          ((BooleanStatsAccumulator) acc).add(value.boolValue());
        } else {
          //          System.out.println("FIELD " + key + " " + value.boolValue()
          //                  + " has acc -> " + acc);
        }
        break;
      case Int:
        if (acc.getClass().equals(NumericStatsAccumulator.class)) {
          ((NumericStatsAccumulator) acc).add((double) value.intValue());
        } else {
          //          System.out.println("FIELD " + key + " " + value.intValue()
          //                  + " has acc -> " + acc);
        }
        break;
      case Long:
        if (acc.getClass().equals(NumericStatsAccumulator.class)) {
          ((NumericStatsAccumulator) acc).add((double) value.longValue());
        } else {
          //          System.out.println("FIELD " + key + " " + value.longValue()
          //                  + " has acc -> " + acc);
        }
        break;
      case Float:
        if (acc.getClass().equals(NumericStatsAccumulator.class)) {
          ((NumericStatsAccumulator) acc).add((double) value.floatValue());
        } else {
          //          System.out.println("FIELD " + key + " " + value.stringValue()
          //                  + " has acc -> " + acc);
        }
        break;
      case Double:
        if (acc.getClass().equals(NumericStatsAccumulator.class)) {
          ((NumericStatsAccumulator) acc).add(value.doubleValue());
        } else {
          //          System.out.println("FIELD " + key + " " + value.doubleValue()
          //                  + " has acc -> " + acc);
        }
        break;
      case String:
        if (acc.getClass().equals(StringStatsAccumulator.class)) {
          ((StringStatsAccumulator) acc).add(value.stringValue());

        } else {
          //          System.out.println("FIELD " + key + " " + value.stringValue()
          //           + " has acc -> " + acc);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown value type " + value.type());
    }
  }

  @Override
  public void processElement(
      final Samples samples, final Context ctx, final Collector<Stats.StatsEvent> out)
      throws Exception {
    System.out.println("PROCESSING " + samples.samples().size() + " samples");
    final var stats = new FieldGroupStats();

    for (final var sample : samples.samples()) {
      for (final var fieldEntry : sample.entrySet()) {
        if (!stats.stats().containsKey(fieldEntry.getKey())) {
          initStatsForField(stats, fieldEntry.getKey(), fieldEntry.getValue().type());
        }

        addValue(stats, fieldEntry.getKey(), fieldEntry.getValue());
      }
    }

    for (final var entry : stats.stats().entrySet()) {
      final var event =
          Stats.StatsEvent.newBuilder()
              .setKey(entry.getKey())
              .setStats(entry.getValue().snapshot())
              .build();

      System.out.println("EVENT " + event);
      out.collect(event);
    }
  }
}
