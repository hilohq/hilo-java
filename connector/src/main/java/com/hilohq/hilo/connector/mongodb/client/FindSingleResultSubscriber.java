package com.hilohq.hilo.connector.mongodb.client;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.CompletableFuture;

public final class FindSingleResultSubscriber<T> implements Subscriber<T> {
    private final CompletableFuture<T> future;
    private Subscription subscription;

    public FindSingleResultSubscriber(final CompletableFuture<T> future) {
        this.future = future;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        this.subscription = subscription;
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
        
    }
}
