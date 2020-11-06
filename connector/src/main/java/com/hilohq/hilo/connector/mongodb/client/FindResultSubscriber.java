package com.hilohq.hilo.connector.mongodb.client;

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
    System.out.println("4");
  }

  @Override
  public void onNext(final T value) {
    System.out.println("5");
    result.add(value);
    System.out.println("10");
    this.subscription.request(1);
    System.out.println("11");
  }

  @Override
  public void onError(final Throwable t) {
    t.printStackTrace();
    System.out.println("6 ");
    future.completeExceptionally(t);
  }

  @Override
  public void onComplete() {
    System.out.println("7");
    future.complete(result);
  }
}
