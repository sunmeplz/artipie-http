/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/npm-adapter/LICENSE.txt
 */
package com.artipie.http.rq.multipart;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Multi-downstream completion handler.
 * @param <T> Type of downstream subscriber
 * @since 1.0
 */
final class Completion<T> {

    /**
     * Fake implementation for tests.
     * @since 1.0
     * @checkstyle AnonInnerLengthCheck (25 lines)
     */
    static final Completion<?> FAKE = new Completion<>(
        new Subscriber<Object>() {
            @Override
            public void onSubscribe(final Subscription sub) {
                // do nothing
            }

            @Override
            public void onNext(final Object next) {
                // do nothing
            }

            @Override
            public void onError(final Throwable err) {
                // do nothing
            }

            @Override
            public void onComplete() {
                // do nothing
            }
        }
    );

    /**
     * Downstream counter.
     */
    private volatile int counter;

    /**
     * Upstream completed flag.
     */
    private volatile boolean completed;

    /**
     * Downstream subscriber.
     */
    private final Subscriber<? super T> downstream;

    /**
     * Synchronization object.
     */
    private final Object lock;

    /**
     * New completion.
     * @param downstream Subscriber reference
     */
    Completion(final Subscriber<? super T> downstream) {
        this.downstream = downstream;
        this.lock = new Object();
    }

    /**
     * Notify downstream item completed.
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    void itemCompleted() {
        System.out.println("item complted");
        synchronized (this.lock) {
            if (--this.counter == 0 && this.completed) {
                System.out.println("complete downstream");
                this.downstream.onComplete();
            }
        }
    }

    /**
     * Notify downstream item started.
     */
    void itemStarted() {
        synchronized (this.lock) {
            assert !this.completed;
            ++this.counter;
        }
    }

    /**
     * Notify upstream completed.
     */
    void upstreamCompleted() {
        synchronized (this.lock) {
            System.out.println("upstream completed");
            this.completed = true;
            if (this.counter == 0) {
                System.out.println("complete downstream");
                this.downstream.onComplete();
            }
        }
    }
}
