package com.artipie.http.misc;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsTrue;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

final class PipelineTest {

    @Test
    void doesNothingOnSubscribe() {
        final Pipeline<?> target = new Pipeline<>();
        final AtomicLong requests = new AtomicLong();
        final AtomicBoolean cancellation = new AtomicBoolean();
        target.onSubscribe(new TestSubscription(requests, cancellation));
        MatcherAssert.assertThat("Requested item", requests.get(), new IsEqual<>(0L));
        MatcherAssert.assertThat("Cancelled", cancellation.get(), new IsEqual<>(false));
    }

    @Test
    void doesNothingOnConnect() {
        final Pipeline<?> target = new Pipeline<>();
        AtomicReference<Subscription> subscription = new AtomicReference<>();
        target.connect(new TestSubscriber(subscription));
        MatcherAssert.assertThat(subscription.get(), new IsNull<>());
    }

    @Test
    void subscribeAndRequestOnSubscribeAndConnect() {
        final Pipeline<?> target = new Pipeline<>();
        AtomicReference<Subscription> subscription = new AtomicReference<>();
        final AtomicBoolean cancellation = new AtomicBoolean();
        final AtomicLong requests = new AtomicLong();
        target.onSubscribe(new TestSubscription(requests, cancellation));
        target.connect(new TestSubscriber(subscription));
        MatcherAssert.assertThat("Not requested one item", requests.get(), new IsEqual<>(1L));
        MatcherAssert.assertThat("Cancelled", cancellation.get(), new IsEqual<>(false));
        MatcherAssert.assertThat("Not subscribed", subscription.get(), new IsEqual<>(target));
    }

    private static final class TestSubscriber implements Subscriber<Object> {

        private final AtomicReference<Subscription> subscription;

        private TestSubscriber(final AtomicReference<Subscription> subscription) {
            this.subscription = subscription;
        }

        @Override
        public void onSubscribe(Subscription sub) {
            if (!this.subscription.compareAndSet(null, sub)) {
                throw new IllegalStateException("Pipeline should not subscribe twice");
            }
        }

        @Override
        public void onNext(Object o) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onComplete() {
        }
    }

    private final class TestSubscription implements Subscription {

        private final AtomicLong requested;
        private final AtomicBoolean cancellation;

        private TestSubscription(final AtomicLong requested, final AtomicBoolean cancellation) {
            this.requested = requested;
            this.cancellation = cancellation;
        }

        @Override
        public void request(long add) {
            this.requested.updateAndGet(old -> {
                if (old + add < 0) {
                    return Long.MAX_VALUE;
                }
                return old + add;
            });
        }

        @Override
        public void cancel() {
            this.cancellation.set(true);
        }
    }
}