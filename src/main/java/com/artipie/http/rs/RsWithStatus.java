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

package com.artipie.http.rs;

import com.artipie.http.Connection;
import com.artipie.http.Response;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.concurrent.CompletionStage;
import org.reactivestreams.Publisher;

/**
 * Response with status.
 * @since 0.1
 */
public final class RsWithStatus implements Response {

    /**
     * Origin response.
     */
    private final Response origin;

    /**
     * Status code.
     */
    private final RsStatus status;

    /**
     * New response with status.
     * @param status Status code
     */
    public RsWithStatus(final RsStatus status) {
        this(StandardRs.EMPTY, status);
    }

    /**
     * Override status code for response.
     * @param origin Response to override
     * @param status Status code
     */
    public RsWithStatus(final Response origin, final RsStatus status) {
        this.origin = origin;
        this.status = status;
    }

    @Override
    public CompletionStage<Void> send(final Connection con) {
        return this.origin.send(new RsWithStatus.ConWithStatus(con, this.status));
    }

    @Override
    public String toString() {
        return String.format("RsWithStatus{status=%s, origin=%s}", this.status, this.origin);
    }

    /**
     * Connection with overridden status code.
     * @since 0.1
     */
    private static final class ConWithStatus implements Connection {

        /**
         * Origin connection.
         */
        private final Connection origin;

        /**
         * New status.
         */
        private final RsStatus status;

        /**
         * Override status code for connection.
         * @param origin Connection
         * @param status Code to override
         */
        ConWithStatus(final Connection origin, final RsStatus status) {
            this.origin = origin;
            this.status = status;
        }

        @Override
        public CompletionStage<Void> accept(
            final RsStatus ignored,
            final Iterable<Entry<String, String>> headers,
            final Publisher<ByteBuffer> body) {
            return this.origin.accept(this.status, headers, body);
        }
    }
}
