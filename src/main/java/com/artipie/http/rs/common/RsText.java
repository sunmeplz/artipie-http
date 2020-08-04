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
package com.artipie.http.rs.common;

import com.artipie.http.Connection;
import com.artipie.http.Headers;
import com.artipie.http.Response;
import com.artipie.http.headers.ContentLength;
import com.artipie.http.headers.ContentType;
import com.artipie.http.rs.RsStatus;
import io.reactivex.Flowable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionStage;

/**
 * Response with text.
 * @since 0.16
 */
public final class RsText implements Response {

    /**
     * Text char sequence.
     */
    private final CharSequence text;

    /**
     * Charset encoding.
     */
    private final Charset encoding;

    /**
     * New text response with {@link CharSequence} and {@code UT8} encoding.
     * @param text Char sequence
     */
    public RsText(final CharSequence text) {
        this(text, StandardCharsets.UTF_8);
    }

    /**
     * New text response with {@link CharSequence} and encoding {@link Charset}.
     * @param text Char sequence
     * @param encoding Charset
     */
    public RsText(final CharSequence text, final Charset encoding) {
        this.text = text;
        this.encoding = encoding;
    }

    @Override
    public CompletionStage<Void> send(final Connection connection) {
        final byte[] bytes = this.text.toString().getBytes(this.encoding);
        return connection.accept(
            RsStatus.OK,
            new Headers.From(
                new ContentType(
                    String.format("text/plain; charset=%s", this.encoding.displayName())
                ),
                new ContentLength(bytes.length)
            ),
            Flowable.just(ByteBuffer.wrap(bytes))
        );
    }
}
