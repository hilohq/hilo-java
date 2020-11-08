package com.hilohq.hilo.structure_learning.parsing.schema;

import com.hilohq.hilo.structure_learning.parsing.value.Value;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Samples {
  private final List<Map<String, Value>> samples;

  public Samples() {
    this(new ArrayList<>());
  }

  public Samples(final List<Map<String, Value>> samples) {
    this.samples = samples;
  }

  public void add(final Map<String, Value> sample) {
    samples.add(sample);
  }

  public List<Map<String, Value>> samples() {
    return samples;
  }
}
