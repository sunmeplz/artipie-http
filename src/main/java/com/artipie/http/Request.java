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

package com.artipie.http;

import java.util.Map;
import java.util.concurrent.Flow;

/**
 * HTTP request.
 * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html">RFC2616</a>
 * @since 0.1
 */
public interface Request {

    /**
     * Request line.
     * <p>
     * See 5.1 section of rfc2616.
     * Request line is {@code Method SP Request-URI SP HTTP-Version CRLF}.
     * </p>
     * @return Line string
     */
    String line();

    /**
     * General headers, request headers and entity headers.
     * <p>
     * See 4.5, 5.3 and 7.1 sections of rfc2616.
     * </p>
     * @return Map of header values by name
     */
    Map<String, Iterable<String>> headers();

    /**
     * Message body, represented as bytes flow.
     * <p>
     * See 4.3 section of rfc2616.
     * </p>
     * @return Bytes flow
     */
    Flow.Publisher<Byte> body();
}
