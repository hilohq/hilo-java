package com.hilohq.hilo.connectors_mongo.client;

import com.hilohq.hilo.connectors_mongo.exceptions.MongoFindException;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FindSingleResultSubscriber<T> implements Subscriber<T> {
  private final CompletableFuture<T> future;
  private Subscription subscription;

  public FindSingleResultSubscriber(final CompletableFuture<T> future) {
    this.future = future;
  }

  @Override
  public void onSubscribe(final Subscription subscription) {
    this.subscription = subscription;
    this.subscription.request(1);
  }

  @Override
  public void onNext(final T value) {
    future.complete(value);
    subscription.cancel();
  }

  @Override
  public void onError(final Throwable t) {
    future.completeExceptionally(t);
  }

  @Override
  public void onComplete() {
    future.completeExceptionally(new MongoFindException("no result found"));
  }
}
