package com.hilohq.hilo.parsers.logparse.log;

public final class TypeInfoFieldStats<T> {
  private final TypeInfoHistogram<T> typeInfoHistogram;

  public TypeInfoFieldStats() {
    this(new TypeInfoHistogram<>());
  }

  public TypeInfoFieldStats(final TypeInfoHistogram<T> typeInfoHistogram) {
    this.typeInfoHistogram = typeInfoHistogram;
  }

  public TypeInfoHistogram<T> typeInfoHistogram() {
    return typeInfoHistogram;
  }

  public TypeInfoFieldStats<T> merge(final TypeInfoFieldStats<T> other) {
    final TypeInfoHistogram<T> histogram = new TypeInfoHistogram<>();

    for (final var entry : other.typeInfoHistogram().entrySet()) {
      histogram.observe(entry.getKey(), entry.getValue());
    }

    for (final var entry : typeInfoHistogram().entrySet()) {
      histogram.observe(entry.getKey(), entry.getValue());
    }

    return new TypeInfoFieldStats<>(histogram);
  }

  public void observe(final T type) {
    typeInfoHistogram.observe(type);
  }
}
