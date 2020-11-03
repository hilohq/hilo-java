package com.hilohq.hilo.hilo_flink.mongodb;

public final class SingleCollectionSupplier<T> implements CollectionSupplier<T> {

  private final String collection;

  public SingleCollectionSupplier(final String collection) {
    this.collection = collection;
  }

  @Override
  public String supply(T value) {
    return collection;
  }
}
