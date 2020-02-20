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
import java.util.concurrent.Flow;
import java.util.regex.Pattern;
import org.cactoos.Text;

/**
 * Multipart request.
 * See
 * <a href="https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html">rfc1341</a>
 * spec.
 * @since 0.4
 * @todo #32:30min Implement this class.
 *  It should read the stream of body chunks and convert it to stream of
 *  request parts. When we detect new request part it should immediately
 *  emmit new part object.
 */
public final class RqMultipart implements Flow.Publisher<RqPart> {

    /**
     * Multipart boundary header.
     * @checkstyle ConstantUsageCheck (500 lines)
     */
    @SuppressWarnings("PMD.UnusedPrivateField")
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
     * @todo #32:30min Add secondary constructor RqMultipart(headers, body)
     *  to convert content type header to Text right in the constructor.
     *  Add a unit test to verify this case.
     */
    public RqMultipart(final Text header, final Flow.Publisher<ByteBuffer> body) {
        this.header = header;
        this.body = body;
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super RqPart> sub) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.header, this.body);
    }
}
