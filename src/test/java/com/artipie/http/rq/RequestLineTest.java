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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RequestLine}.
 * <p>
 * See 5.1.2 section of
 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html">RFC2616</a>
 * </p>
 * @since 0.1
 */
public final class RequestLineTest {
    @Test
    void parsesMethodName() throws Exception {
        MatcherAssert.assertThat(
            new RequestLine("TRACE /foo HTTP/1.1").method(),
            Matchers.equalTo("TRACE")
        );
    }

    @Test
    void parsesAsteriskUri() throws Exception {
        MatcherAssert.assertThat(
            new RequestLine("GET * HTTP/1.1").uri(),
            Matchers.equalTo(URI.create("*"))
        );
    }

    @Test
    void parsesAbsoluteUri() throws Exception {
        MatcherAssert.assertThat(
            new RequestLine("GET http://www.w3.org/pub/WWW/TheProject.html HTTP/1.1").uri(),
            Matchers.equalTo(URI.create("http://www.w3.org/pub/WWW/TheProject.html"))
        );
    }

    @Test
    void parsesAbsolutePath() throws Exception {
        MatcherAssert.assertThat(
            new RequestLine("GET /pub/WWW/TheProject.html HTTP/1.1").uri(),
            Matchers.equalTo(URI.create("/pub/WWW/TheProject.html"))
        );
    }

    @Test
    void parsesHttpVersion() throws Exception {
        MatcherAssert.assertThat(
            new RequestLine("PUT * HTTP/1.1").version(),
            Matchers.equalTo("HTTP/1.1")
        );
    }
}
