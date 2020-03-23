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
package com.artipie.http;

import java.nio.ByteBuffer;
import java.util.Map;
import org.reactivestreams.Publisher;

/**
 * Arti-pie slice.
 * <p>
 * Slice is a part of Artipie server.
 * Each Artipie adapter implements this interface to expose
 * repository HTTP API.
 * Artipie main module joins all slices together into solid web server.
 * </p>
 * @since 0.1
 */
public interface Slice {

    /**
     * Respond to a http request.
     * @param line The request line
     * @param headers The request headers
     * @param body The request body
     * @return The response.
     */
    Response response(
        String line,
        Iterable<Map.Entry<String, String>> headers,
        Publisher<ByteBuffer> body
    );

    /**
     * SliceWrap is a simple decorative envelope for Slice.
     *
     * @since 0.7
     */
    abstract class Wrap implements Slice {

        /**
         * Origin slice.
         */
        private final Slice slice;

        /**
         * Ctor.
         *
         * @param slice Slice.
         */
        protected Wrap(final Slice slice) {
            this.slice = slice;
        }

        @Override
        public final Response response(
            final String line,
            final Iterable<Map.Entry<String, String>> headers,
            final Publisher<ByteBuffer> body) {
            return this.slice.response(line, headers, body);
        }
    }
}
