package com.hilohq.hilo.connector.mongodb.client;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.CompletableFuture;

public final class ResultSubscriber<T> implements Subscriber<T> {
    private final CompletableFuture<Void> future;
    Subscription subscription;

    ResultSubscriber(final CompletableFuture<Void> future) {
        this.future = future;
    }

    @Override
    public void onSubscribe(final Subscription s) {
        this.subscription = s;
        this.subscription.request(10);
    }

    @Override
    public void onNext(final T value) {
        subscription.request(10);
    }

    @Override
    public void onError(final Throwable t) {
        future.completeExceptionally(t);
    }

    @Override
    public void onComplete() {
        future.complete(null);
    }
}
