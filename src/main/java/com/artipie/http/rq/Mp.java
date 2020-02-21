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

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Flow;

/**
 * A multipart parser. Parses a Flow of Bytes into a flow of Multiparts
 * See
 * <a href="https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html">rfc1341</a>
 * spec.
 * @since 0.4
 */
public final class Mp implements Flow.Publisher<Part> {

    /**
     * Content type header.
     * <p>
     * It's the main header of multipart request.
     * </p>
     */
    private final Callable<String> boundary;

    /**
     * Byte buffer body publisher.
     */
    private final Flow.Publisher<ByteBuffer> body;

    /**
     * Ctor.
     * @param boundary Multipart body boundary
     * @param body A flow of bytes
     */
    public Mp(final String boundary,
              final Flow.Publisher<ByteBuffer> body) {
        this.boundary = () -> boundary;
        this.body = body;
    }

    /**
     * Ctor.
     * @param headers Content type header value
     * @param body A flow of bytes
     */
    public Mp(final Iterable<Map.Entry<String, String>> headers,
              final Flow.Publisher<ByteBuffer> body) {
        this.boundary = () -> {
            throw new IllegalStateException("boundary parsing from headers are not implemented");
        };
        this.body = body;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Part> subscriber) {
        throw new IllegalStateException("subscription is not implemented");
    }
}
