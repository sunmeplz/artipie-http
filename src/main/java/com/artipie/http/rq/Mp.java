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
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

/**
 * A multipart parser. Parses a Flow of Bytes into a flow of Multiparts
 * See
 * <a href="https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html">rfc1341</a>
 * spec.
 * @since 0.4
 */
public final class Mp implements Flow.Processor<ByteBuffer, Part> {

    private static final String CRLF = "\r\n";

    /**
     * Content type header.
     * <p>
     * It's the main header of multipart request.
     * </p>
     */
    private final Supplier<String> boundary;

    /**
     * Ctor.
     *
     * @param boundary Multipart body boundary
     */
    public Mp(final String boundary) {
        this.boundary = () -> boundary;
    }

    /**
     * Ctor.
     *
     * @param headers Content type header value
     */
    public Mp(final Iterable<Map.Entry<String, String>> headers) {
        this.boundary = () -> {
            final Pattern pattern = Pattern.compile("boundary=(\\w+)");
            final String type = StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> header.getKey().equalsIgnoreCase("content-type"))
                .map(Map.Entry::getValue)
                .findFirst()
                .get();
            final Matcher matcher = pattern.matcher(type);
            matcher.find();
            return matcher.group(1);
        };
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Part> subscriber) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void onNext(ByteBuffer item) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void onError(Throwable throwable) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void onComplete() {
        throw new IllegalStateException("not implemented");
    }
}
