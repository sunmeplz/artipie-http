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

package com.artipie.http.async;

import com.artipie.http.Response;
import com.artipie.http.Slice;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.reactivestreams.Publisher;

/**
 * Asynchronous {@link Slice} implementation.
 * <p>
 * This slice encapsulates {@link CompletionStage} of {@link Slice} and returns {@link Response}.
 * </p>
 * @since 0.4
 * @todo #41:30min Add unit tests for AsyncSlice and RsAsync.
 *  Tests should verify the positive case, when slice and response
 *  completes normally; And exceptional behavior, when 500 error should be
 *  sent to connection.
 */
public final class AsyncSlice implements Slice {

    /**
     * Async slice.
     */
    private final CompletionStage<Slice> slice;

    /**
     * Ctor.
     * @param slice Async slice.
     */
    public AsyncSlice(final CompletionStage<Slice> slice) {
        this.slice = slice;
    }

    @Override
    public Response response(final String line,
        final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        return connection -> this.slice.thenCompose(
            target -> target.response(line, headers, body).send(connection)
        );
    }
}
