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
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.reactivestreams.Publisher;

/**
 * RsFull, response with status code, headers and body.
 *
 * @since 0.8
 * @checkstyle ParameterNumberCheck (500 lines)
 */
public final class RsFull implements Response {

    /**
     * Origin response.
     */
    private final Response origin;

    /**
     * Ctor.
     * @param origin Origin response
     * @param status Status code
     * @param headers Headers
     * @param body Response body
     */
    public RsFull(
        final Response origin,
        final RsStatus status,
        final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        this.origin = new RsWithStatus(
            new RsWithHeaders(
                new RsWithBody(
                    origin, body
                ), headers
            ), status
        );
    }

    @Override
    public CompletionStage<Void> send(final Connection connection) {
        return this.origin.send(connection);
    }
}
