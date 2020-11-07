package com.hilohq.hilo.statistics.stats;

import com.google.protobuf.Message;

public interface Snapshot<T extends Message> {
  T snapshot();
}
