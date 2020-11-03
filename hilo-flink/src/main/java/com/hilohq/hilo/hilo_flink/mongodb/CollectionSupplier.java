package com.hilohq.hilo.hilo_flink.mongodb;

import java.io.Serializable;

@FunctionalInterface
public interface CollectionSupplier<T> extends Serializable {
  String supply(final T value);
}
