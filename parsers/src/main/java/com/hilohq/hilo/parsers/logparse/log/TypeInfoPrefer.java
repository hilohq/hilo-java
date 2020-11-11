package com.hilohq.hilo.parsers.logparse.log;

public final class TypeInfoPrefer<T> {
  public TypeInfoPrefer() {}

  public T fromStats(final TypeInfoFieldStats<T> fieldStats, final T defaultType) {
    var preferredType = defaultType;
    var count = 0L;

    for (final var entries : fieldStats.typeInfoHistogram().entrySet()) {
      if (entries.getValue() > count) {
        count = entries.getValue();
        preferredType = entries.getKey();
      }
    }

    return preferredType;
  }
}
