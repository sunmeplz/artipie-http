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

import com.google.common.collect.EvictingQueue;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.nio.ByteBuffer;

/**
 *
 */
public class ByteByByteSplit extends ByteStreamSplit {

    /**
     * A ring buffer with bytes.
     */
    private final EvictingQueue<Byte> ring;

    /**
     * Ctor.
     * @param delim The delim.
     */
    public ByteByByteSplit(final byte[] delim) {
        super(delim);
        this.ring = EvictingQueue.create(delim.length);
    }

    /// Publisher ///

    @Override
    public void subscribe(Subscriber<? super Publisher<ByteBuffer>> s) {

    }

    /// Subscriber ///

    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(ByteBuffer byteBuffer) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
