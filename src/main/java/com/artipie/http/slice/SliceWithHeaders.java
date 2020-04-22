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
package com.artipie.http.slice;

import com.artipie.http.Headers;
import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.rs.RsWithHeaders;
import java.nio.ByteBuffer;
import java.util.Map;
import org.reactivestreams.Publisher;

/**
 * Decorator for {@link Slice} which adds headers to the origin.
 * @since 0.9
 */
public final class SliceWithHeaders implements Slice {

    /**
     * Origin slice.
     */
    private final Slice origin;

    /**
     * Headers.
     */
    private final Headers headers;

    /**
     * Ctor.
     * @param origin Origin slice
     * @param headers Headers
     */
    public SliceWithHeaders(final Slice origin, final Headers headers) {
        this.origin = origin;
        this.headers = headers;
    }

    @Override
    public Response response(final String line, final Iterable<Map.Entry<String, String>> hdrs,
        final Publisher<ByteBuffer> body) {
        return new RsWithHeaders(
            this.origin.response(line, hdrs, body),
            this.headers
        );
    }
}
