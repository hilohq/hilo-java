package com.hilohq.hilo.features.stats.numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Window {

  private final List<Double> window;
  private final int maxSize;

  public Window(final int size) {
    this.window = new ArrayList<>(size);
    this.maxSize = size;
  }

  public void observe(final double value) {
    if (window.size() == maxSize) {
      window.remove(0);
    }
    window.add(value);
  }

  public int maxSize() {
    return maxSize;
  }

  public Optional<Double> peek() {
    if (window.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(window.get(window.size() - 1));
    }
  }

  public double get(int index) {
    return window.get(index);
  }

  public int size() {
    return window.size();
  }

  public List<Double> values() {
    return window;
  }

  public void clear() {
    window.clear();
  }
}
