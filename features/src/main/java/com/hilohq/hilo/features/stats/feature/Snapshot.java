package com.hilohq.hilo.features.stats.feature;

import com.google.protobuf.Message;

public interface Snapshot<T extends Message> {
  T snapshot();
}
