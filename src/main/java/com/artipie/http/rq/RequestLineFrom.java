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

import java.net.URI;

/**
 * Request line helper object.
 * <p>
 * See 5.1 section of RFC2616:<br/>
 * The Request-Line begins with a method token,
 * followed by the Request-URI and the protocol version,
 * and ending with {@code CRLF}.
 * The elements are separated by SP characters.
 * No {@code CR} or {@code LF} is allowed except in the final {@code CRLF} sequence.
 * <br/>
 * {@code Request-Line = Method SP Request-URI SP HTTP-Version CRLF}.
 * </p>
 * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html">RFC2616</a>
 * @since 0.1
 */
public final class RequestLineFrom {

    /**
     * HTTP request line.
     */
    private final String line;

    /**
     * Primary ctor.
     * @param line HTTP request line
     */
    public RequestLineFrom(final String line) {
        this.line = line;
    }

    /**
     * Request method.
     * @return Method name
     */
    public RqMethod method() {
        final String string = this.part(0);
        return RqMethod.ALL
            .stream()
            .filter(method -> method.value().equals(string))
            .findAny()
            .orElseThrow(
                () -> new IllegalStateException(String.format("Unknown method: '%s'", string))
            );
    }

    /**
     * Request URI.
     * @return URI of the request
     */
    public URI uri() {
        return URI.create(this.part(1));
    }

    /**
     * HTTP version.
     * @return HTTP version string
     */
    public String version() {
        return this.part(2);
    }

    /**
     * Part of request line.
     * @param idx Part index
     * @return Part string
     */
    private String part(final int idx) {
        final int pcount = 3;
        final String[] parts = this.line.trim().split("\\s");
        if (parts.length == pcount) {
            return parts[idx];
        } else {
            throw new IllegalArgumentException(
                String.format("Invalid HTTP request line \n%s", this.line)
            );
        }
    }
}
