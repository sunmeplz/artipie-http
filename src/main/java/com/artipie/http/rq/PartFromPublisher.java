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
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * Turn {@link org.reactivestreams.Publisher} of bytes into a {@link Part}.
 *
 * @todo #32:90min Implement delyaed publisher and header parsing.
 *  Implemented delayed publisher and header parsing. First, headers should be parsed, then we can
 *  emit the rest of the bytes.
 *
 * @since 0.7.2
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.UnusedPrivateField", "PMD.SingularField"})
public final class PartFromPublisher implements Part {

    /**
     * The source.
     */
    private final Publisher<ByteBuffer> publisher;

    /**
     * Ctor.
     *
     * @param publisher The publisher to create part from.
     */
    public PartFromPublisher(final Publisher<ByteBuffer> publisher) {
        this.publisher = publisher;
    }

    @Override
    public Iterable<Map.Entry<String, String>> headers() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void subscribe(final Subscriber<? super ByteBuffer> sub) {
        throw new IllegalStateException("Not implemented");
    }

}
