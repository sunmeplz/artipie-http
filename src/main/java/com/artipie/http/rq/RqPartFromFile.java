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
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Flow;

/**
 * Request multipart part from file.
 * @since 0.4
 * @todo #32:30min Implement this class.
 *  It should read request data from file,
 *  split it into head and body parts (by \r\n symbol)
 *  and parse headers to `headers()` method and publish other part of
 *  file via Flow publisher on subscribe.
 */
public final class RqPartFromFile implements RqPart {

    /**
     * Part path.
     */
    private final Path path;

    /**
     * New part from file.
     * @param path File path
     */
    public RqPartFromFile(final Path path) {
        this.path = path;
    }

    @Override
    public Iterable<Map.Entry<String, String>> headers() {
        throw new IllegalStateException("headers() are not implemented");
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
        throw new IllegalStateException("subscribe() are not implemented");
    }

    @Override
    public String toString() {
        return this.path.toString();
    }
}
