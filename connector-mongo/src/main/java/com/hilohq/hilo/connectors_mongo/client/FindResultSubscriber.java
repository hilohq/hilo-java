package com.hilohq.hilo.connectors_mongo.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FindResultSubscriber<T> implements Subscriber<T> {
  private final CompletableFuture<Collection<T>> future;
  private final Collection<T> result;
  private Subscription subscription;

  public FindResultSubscriber(final CompletableFuture<Collection<T>> future) {
    this.future = future;
    this.result = new ArrayList<>();
  }

  @Override
  public void onSubscribe(final Subscription subscription) {
    this.subscription = subscription;
    this.subscription.request(1);
  }

  @Override
  public void onNext(final T value) {
    result.add(value);
    this.subscription.request(1);
  }

  @Override
  public void onError(final Throwable t) {
    future.completeExceptionally(t);
  }

  @Override
  public void onComplete() {
    future.complete(result);
  }
}
