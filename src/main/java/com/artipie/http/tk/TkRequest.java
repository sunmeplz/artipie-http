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

package com.artipie.http.tk;

import com.artipie.http.Request;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.takes.rq.RqHeaders;

/**
 * Takes request wrapper.
 * @since 0.1
 */
public final class TkRequest implements Request {

    /**
     * Takes request.
     */
    private final org.takes.Request tkreq;

    /**
     * Ctor.
     * @param tkreq Takes request
     */
    public TkRequest(final org.takes.Request tkreq) {
        this.tkreq = tkreq;
    }

    @Override
    public String line() throws IOException {
        return this.tkreq.head().iterator().next();
    }

    @Override
    public Map<String, Iterable<String>> headers() throws IOException {
        final RqHeaders.Smart req = new RqHeaders.Smart(this.tkreq);
        return new MapOf<>(
            name -> new MapEntry<>(name, req.header(name)),
            req.names()
        );
    }

    @Override
    public Publisher<Byte> body() throws IOException {
        return new TkRequest.InputStreamPublisher(this.tkreq.body());
    }

    /**
     * Flow subscription for request body.
     * @since 0.1
     * @todo #3:30min Implement this class.
     *  It should read requested amount of bytes on `request` method call
     *  and submit it to the receiver.
     *  When the stream is ended it should notify the receiver via `onComplete` call.
     *  On failure it should call `onError` of receiver.
     *  If cancellation requested via `cancel` method of Subscription it should close the
     *  stream and exit. Also, the stream should be closed when complete.
     */
    private static final class BodySubstription implements Subscription {

        /**
         * Buffer size for stream reading.
         */
        private static final int BUF_SIZE = 1024 * 8;

        /**
         * Request input stream.
         */
        private final InputStream stream;

        /**
         * Flow subscriber as receiver.
         */
        private final Subscriber<? super Byte> receiver;

        /**
         * Flow subscriptions from request input stream.
         * @param stream Request input stream
         * @param receiver Flow subscriber as receiver
         */
        BodySubstription(final InputStream stream, final Subscriber<? super Byte> receiver) {
            this.stream = stream;
            this.receiver = receiver;
        }

        @Override
        public void request(final long bytes) {
            try {
                this.read(bytes);
            } catch (final IOException | IllegalArgumentException err) {
                this.receiver.onError(err);
            }
        }

        @Override
        public void cancel() {
            try {
                this.stream.close();
            } catch (final IOException err) {
                this.receiver.onError(err);
            }
        }

        /**
         * Read bytes from stream into receiver.
         * @param bytes Amount of bytes to read
         * @throws IOException On stream error
         */
        private void read(final long bytes) throws IOException {
            if (bytes <= 0) {
                throw new IllegalArgumentException(String.format("can't request %d bytes", bytes));
            }
            final byte[] buf = new byte[TkRequest.BodySubstription.BUF_SIZE];
            long total = 0;
            while (total < bytes) {
                final int len;
                if (total + TkRequest.BodySubstription.BUF_SIZE <= bytes) {
                    len = TkRequest.BodySubstription.BUF_SIZE;
                } else {
                    len = (int) (bytes - total);
                }
                final int read = this.stream.read(buf, 0, len);
                total += read;
                if (read == -1) {
                    this.stream.close();
                    this.receiver.onComplete();
                    break;
                } else {
                    for (int pos = 0; pos < read; ++pos) {
                        this.receiver.onNext(buf[pos]);
                    }
                }
            }
        }
    }

    /**
     * Flow publisher for input stream.
     * <p>
     * It encapsulates the {@link InputStream} and implements {@link Publisher}
     * interface from JDK9 Flow API.
     * </p>
     * @since 0.1
     */
    private static final class InputStreamPublisher implements Publisher<Byte> {

        /**
         * The input stream.
         */
        private final InputStream stream;

        /**
         * Publisher from request stream.
         * @param stream Input stream
         */
        InputStreamPublisher(final InputStream stream) {
            this.stream = stream;
        }

        @Override
        public void subscribe(final Subscriber<? super Byte> subscriber) {
            subscriber.onSubscribe(new TkRequest.BodySubstription(this.stream, subscriber));
        }
    }
}
