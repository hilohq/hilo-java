package com.hilohq.hilo.features.stats.feature;

import com.hilohq.hilo.parsers.value.Value;
import com.hilohq.hilo.protobufs.structure.FeatureOuterClass;

public interface FeatureStats {
  void observe(Value value);

  FeatureOuterClass.FeatureStats snapshot();
}
