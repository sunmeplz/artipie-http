/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/npm-adapter/LICENSE.txt
 */
package com.artipie.http.rq.multipart;

import com.artipie.ArtipieException;
import com.artipie.http.misc.ByteBufferTokenizer;
import com.artipie.http.misc.DummySubscription;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Multipart parts publisher.
 *
 * @since 1.0
 * @checkstyle MethodBodyCommentsCheck (500 lines)
 */
final class MultiParts implements Processor<ByteBuffer, RqMultipart.Part>,
    ByteBufferTokenizer.Receiver {

    /**
     * Downstream references of parts.
     */
    private final AtomicReference<Subscriber<? super RqMultipart.Part>> downstream;

    /**
     * Upstream subscription reference.
     */
    private final AtomicReference<Subscription> upstream;

    /**
     * Parts tokenizer.
     */
    private final ByteBufferTokenizer tokenizer;

    /**
     * Subscription executor service.
     */
    private final ExecutorService exec;

    /**
     * Current part.
     */
    private volatile MultiPart current;

    /**
     * State flags.
     */
    private final State state;

    /**
     * Completion handler.
     */
    private final Completion<?> completion;

    /**
     * New multipart parts publisher for upstream publisher.
     * @param boundary Boundary token delimiter of parts
     */
    MultiParts(final String boundary) {
        this.tokenizer = new ByteBufferTokenizer(
            this, boundary.getBytes(StandardCharsets.US_ASCII)
        );
        this.exec = Executors.newSingleThreadExecutor();
        this.downstream = new AtomicReference<>();
        this.upstream = new AtomicReference<>();
        this.completion = new Completion<>(this.downstream);
        this.state = new State();
    }

    /**
     * Subscribe publisher to this processor asynchronously.
     * @param pub Upstream publisher
     */
    public void subscribeAsync(final Publisher<ByteBuffer> pub) {
        this.exec.submit(() -> pub.subscribe(this));
    }

    @Override
    public void subscribe(final Subscriber<? super RqMultipart.Part> sub) {
        if (this.downstream.compareAndSet(null, sub)) {
            sub.onSubscribe(this.upstream.get());
        } else {
            sub.onSubscribe(DummySubscription.VALUE);
            sub.onError(new IllegalStateException("Downstream already connected"));
            return;
        }
        if (this.upstream.get() != null) {
            this.upstream.get().request(1);
        }
    }

    @Override
    public void onSubscribe(final Subscription sub) {
        if (!this.upstream.compareAndSet(null, sub)) {
            this.onError(new IllegalStateException("Can't subscribe twice"));
        }
        if (this.downstream.get() != null) {
            sub.request(1);
        }
    }

    @Override
    public void onNext(final ByteBuffer chunk) {
        this.tokenizer.push(chunk);
    }

    @Override
    public void onError(final Throwable err) {
        this.downstream.getAndSet(null).onError(
            new ArtipieException("Upstream failed", err)
        );
    }

    @Override
    public void onComplete() {
        this.completion.upstreamCompleted();
    }

    @Override
    public void receive(final ByteBuffer next, final boolean end) {
        synchronized (this.upstream) {
            this.state.patch(next, end);
            if (this.state.shouldIgnore()) {
                this.upstream.get().request(1L);
                return;
            }
            if (this.state.started()) {
                this.completion.itemStarted();
                this.current = new MultiPart(
                    this.upstream.get(), this.completion,
                    part -> {
                        final Subscriber<? super RqMultipart.Part> subscriber =
                            this.downstream.get();
                        this.exec.submit(() -> subscriber.onNext(part));
                    },
                    this.exec
                );
            }
            this.current.push(next);
            if (this.state.ended()) {
                this.current.flush();
            }
        }
    }
}
