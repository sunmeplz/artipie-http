/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/npm-adapter/LICENSE.txt
 */
package com.artipie.http.rq.multipart;

import com.artipie.asto.ext.PublisherAs;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.functions.Functions;
import io.reactivex.subjects.SingleSubject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Test case for {@link MultiPart}.
 *
 * @since 1.0
 */
final class MultiPartTest {

    @Test
    void parsePart() throws Exception {
        SingleSubject<RqMultipart.Part> subj = SingleSubject.create();
        final MultiPart part = new MultiPart(Completion.FAKE, subj::onSuccess);
        Executors.newCachedThreadPool().submit(
                () -> {
                    for (String chunk : Arrays.asList(
                            "Content-l", "ength", ": 24\r\n",
                            "Con", "tent-typ", "e: ", "appl", "ication/jso", "n\r\n\r\n{\"foo",
                            "\": \"b", "ar\", ", "\"val\": [4]}"
                    )) {
                        part.push(ByteBuffer.wrap(chunk.getBytes(StandardCharsets.US_ASCII)));
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            part.cancel();
                            return;
                        }
                    }
                    part.flush();
                }
        );
        MatcherAssert.assertThat(
                new PublisherAs(subj.flatMapPublisher(Functions.identity())).string(StandardCharsets.US_ASCII)
                        .toCompletableFuture().get(),
                Matchers.equalTo("{\"foo\": \"bar\", \"val\": [4]}")
        );
    }

    /**
     * Part subscriber.
     *
     * @since 1.0
     */
    private static final class Subscriber implements FlowableSubscriber<ByteBuffer> {

        /**
         * Part future.
         */
        private final CompletableFuture<RqMultipart.Part> future;

        /**
         * Current part.
         */
        private volatile MultiPart part;

        /**
         * New subscriber.
         *
         * @param future Part future
         */
        Subscriber(final CompletableFuture<RqMultipart.Part> future) {
            this.future = future;
        }

        @Override
        public void onSubscribe(final Subscription subscription) {
            synchronized (this.future) {
                if (this.part != null) {
                    this.future.completeExceptionally(
                            new IllegalStateException("Subscribed twice")
                    );
                    return;
                }
                Completion<ByteBuffer> completion = new Completion<>(this);
                completion.itemStarted();
                this.part = new MultiPart(
                        completion, this.future::complete
                );
                subscription.request(Long.MAX_VALUE);
            }
        }

        @Override
        public void onNext(final ByteBuffer buffer) {
            this.part.push(buffer);
        }

        @Override
        public void onError(final Throwable err) {
            this.future.completeExceptionally(err);
        }

        @Override
        public void onComplete() {
            this.part.flush();
        }
    }
}
