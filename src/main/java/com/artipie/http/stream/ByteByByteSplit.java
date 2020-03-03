/*
 * MIT License
 *
 * Copyright (c) 2020 Artipie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.artipie.http.stream;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.ArrayUtils;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Byte stream split implementation based on Circular buffer of bytes.
 *
 * @since 0.4
 */
public class ByteByByteSplit extends ByteStreamSplit {

    /**
     * A ring buffer with bytes. Used for delimiter findings.
     */
    private final CircularFifoQueue<Byte> ring;
    
    /**
     * The splitted buffers. Empty means delimiter.
     */
    private final LinkedBlockingQueue<Optional<ByteBuffer>> storage;
    
    /**
     * The upstream to request elements from.
     */
    private final AtomicReference<Subscription> upstream;
    
    /**
     * Downstream to emit elements to.
     */
    private final AtomicReference<Subscriber<? super Publisher<ByteBuffer>>> downstream;
    
    /**
     * Downstream of a downstream element.
     */
    private final AtomicReference<Subscriber<? super ByteBuffer>> downDownstream;
    
    /**
     * Is this processor already started?
     */
    private final AtomicBoolean started;
    
    /**
     * Has upstream been terminated.
     */
    private final AtomicBoolean upstreamTerminated;
    
    /**
     * The downstream demand.
     */
    private final AtomicLong downDemand;
    
    /**
     * The down downstream demand.
     */
    private final AtomicLong downDownDemand;

    /**
     * Ctor.
     *
     * @param delimiter The delimiter.
     */
    public ByteByByteSplit(final byte[] delimiter) {
        super(delimiter);
        this.ring = new CircularFifoQueue<>(delimiter.length);
        this.upstream = new AtomicReference<>();
        this.downstream = new AtomicReference<>();
        this.started = new AtomicBoolean(false);
        this.downDownstream = new AtomicReference<>();
        this.storage = new LinkedBlockingQueue<>();
        this.upstreamTerminated = new AtomicBoolean(false);
        this.downDemand = new AtomicLong(0);
        this.downDownDemand = new AtomicLong(0);
    }
    
    @Override
    public void subscribe(final Subscriber<? super Publisher<ByteBuffer>> sub) {
        if (this.downstream.get() != null) {
            throw new IllegalStateException("Only one subscription is allowed");
        }
        this.downstream.set(sub);
        sub.onSubscribe(new Subscription() {
            @Override
            public void request(final long ask) {
                ByteByByteSplit.this.downDemand.updateAndGet(operand -> operand + ask);
                ByteByByteSplit.this.upstream.get().request(ask);
            }

            @Override
            public void cancel() {
                throw new IllegalStateException("Cancel is not allowed");
            }
        });
        this.tryToStart();
    }
    
    @Override
    public void onSubscribe(final Subscription sub) {
        if (this.downstream.get() != null) {
            throw new IllegalStateException("Only one subscription is allowed");
        }
        this.upstream.set(sub);
    }

    @Override
    public synchronized void onNext(final ByteBuffer byteBuffer) {
        final byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        ByteBuffer current = this.bufWithInitMark(bytes.length);
        for (final byte each : bytes) {
            final boolean eviction = ring.isAtFullCapacity();
            if (eviction) {
                final Byte last = ring.get(delim.length - 1);
                current.put(last);
            }
            ring.add(each);
            final byte[] primitive = ringBytes();
            if (Arrays.equals(delim, primitive)) {
                ring.clear();
                current.limit(current.position());
                current.reset();
                this.emit(Optional.of(current));
                this.emit(Optional.empty());
                current = bufWithInitMark(bytes.length);
            }
        }
        current.limit(current.position());
        current.reset();
        this.emit(Optional.of(current));
    }

    @Override
    public void onError(final Throwable throwable) {
        this.upstreamTerminated.set(true);
        final Subscriber<? super Publisher<ByteBuffer>> subscriber = this.downstream.get();
        if (subscriber != null) {
            subscriber.onError(throwable);
        }
    }
    
    @Override
    public void onComplete() {
        this.upstreamTerminated.set(true);
        this.emit(Optional.of(ByteBuffer.wrap(ringBytes())));
    }
    
    private ByteBuffer bufWithInitMark(int size) {
        ByteBuffer current = ByteBuffer.allocate(size);
        current.mark();
        return current;
    }
    
    private byte[] ringBytes() {
        return ArrayUtils.toPrimitive(ring.stream().toArray(Byte[]::new));
    }

    private void tryToStart() {
        if (this.downstream.get() != null &&
            this.upstream.get() != null &&
            this.started.compareAndSet(false, true)) {
            this.emitNextSubSub();
        }
    }

    private synchronized void emit(final Optional<ByteBuffer> buffer) {
        this.storage.add(buffer);
        meetDemand();
    }

    private synchronized void meetDemand() {
        if (this.downDownstream.get() != null) {
            while (this.downDownDemand.get() > 0 && this.storage.size() > 0) {
                this.downDownDemand.decrementAndGet();
                final Optional<ByteBuffer> poll = this.storage.poll();
                if (poll.isPresent()) {
                    downDownstream.get().onNext(poll.get());
                } else {
                    downDownstream.get().onComplete();
                    emitNextSubSub();
                }
            }
            if (this.upstreamTerminated.get()) {
                downDownstream.get().onComplete();
                downstream.get().onComplete();
            }
        }
    }

    private void emitNextSubSub() {
        Publisher<ByteBuffer> pub = sub -> {
            downDownstream.set(sub);
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    downDownDemand.updateAndGet(operand -> operand + n);
                    meetDemand();
                }

                @Override
                public void cancel() {

                }
            });
        };
        downstream.get().onNext(pub);
    }
}
