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

package com.artipie.http.rq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cactoos.Text;
import org.cactoos.scalar.Unchecked;

/**
 * Multipart request.
 * See
 * <a href="https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html">rfc1341</a>
 * spec.
 * @since 0.4
 */
public final class RqMultipart implements Flow.Publisher<RqPart> {

    /**
     * Default buffer size.
     */
    private static final int BUF_SIZE = 1024 * 8;

    /**
     * Multipart boundary header.
     */
    private static final Pattern BOUNDARY = Pattern.compile(".*[^a-z]boundary=([^;]+).*");

    /**
     * Content type header.
     * <p>
     * It's the main header of multipart request.
     * </p>
     */
    private final Text header;

    /**
     * Byte buffer body publisher.
     */
    private final Flow.Publisher<ByteBuffer> body;

    /**
     * Ctor.
     * @param header Content type header value
     * @param body Publisher
     */
    public RqMultipart(final Text header, final Flow.Publisher<ByteBuffer> body) {
        this.header = header;
        this.body = body;
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super RqPart> sub) {
        final Matcher matcher =
            BOUNDARY.matcher(new Unchecked<>(this.header::asString).value());
        if (!matcher.matches()) {
            sub.onSubscribe(
                new Subscription() {
                    @Override
                    public void request(final long req) {
                        sub.onError(
                            new IllegalStateException(
                                "invalid boundary for content type header"
                            )
                        );
                    }

                    @Override
                    public void cancel() {
                        // @checkstyle MethodBodyCommentsCheck (1 line)
                        // nothing to do
                    }
                }
            );
            return;
        }
        final byte[] boundary = String.format("--%s", matcher.group(1))
            .getBytes(StandardCharsets.UTF_8);
        final Flow.Processor<ByteBuffer, RqPart> proc = new RqMultipart.Sub(boundary);
        this.body.subscribe(proc);
        proc.subscribe(sub);
    }

    /**
     * Flow processor to convert multipart chunks to request parts.
     * @since 0.4
     */
    private static final class Sub implements Flow.Processor<ByteBuffer, RqPart> {

        /**
         * Local state for uncompleted chunk.
         */
        private final ByteBuffer state;

        /**
         * Multipart processor.
         */
        private final Multiparts parts;

        /**
         * Flow subscriber.
         */
        private Subscriber<? super RqPart> subscriber;

        /**
         * Flow subscription.
         */
        private Subscription subscription;

        /**
         * New processor with fixed boundary.
         * @param boundary Boundary bytes
         */
        Sub(final byte[] boundary) {
            this.parts = new Multiparts(ByteBuffer.wrap(boundary));
            this.state = ByteBuffer.allocate(RqMultipart.BUF_SIZE);
        }

        @Override
        public void onSubscribe(final Subscription sub) {
            this.subscription = sub;
            this.request();
        }

        @Override
        public void onNext(final ByteBuffer item) {
            this.state.put(item).flip();
            try {
                while (this.parts.push(this.state)) {
                    this.state.compact().flip();
                }
            } catch (final IOException err) {
                this.subscriber.onError(err);
                return;
            }
            this.state.compact();
            for (final Path part : this.parts.popAll()) {
                this.subscriber.onNext(new RqPartFromFile(part));
            }
            this.subscription.request(1L);
        }

        @Override
        public void onError(final Throwable err) {
            this.subscriber.onError(err);
        }

        @Override
        public void onComplete() {
            if (this.state.hasRemaining()) {
                this.subscriber.onError(
                    new IllegalStateException(
                        "Subscriber completed at the middle of part receiving"
                    )
                );
            } else {
                this.subscriber.onComplete();
            }
        }

        @Override
        public void subscribe(final Subscriber<? super RqPart> sub) {
            this.subscriber = sub;
            this.request();
        }

        /**
         * Request initial item.
         */
        private void request() {
            if (this.subscriber != null && this.subscription != null) {
                this.subscription.request(1L);
            }
        }
    }
}
