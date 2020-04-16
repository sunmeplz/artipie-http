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
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RequestLine}.
 * <p>
 * See 5.1.2 section of
 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html">RFC2616</a>
 * </p>
 * @since 0.1
 */
public final class RequestLineFromTest {
    @Test
    void parsesMethodName() {
        MatcherAssert.assertThat(
            new RequestLineFrom("TRACE /foo HTTP/1.1\r\n").method(),
            Matchers.equalTo(RqMethod.TRACE)
        );
    }

    @Test
    void parsesAsteriskUri() {
        MatcherAssert.assertThat(
            new RequestLineFrom("GET * HTTP/1.1\r\n").uri(),
            Matchers.equalTo(URI.create("*"))
        );
    }

    @Test
    void parsesAbsoluteUri() {
        MatcherAssert.assertThat(
            new RequestLineFrom("GET http://www.w3.org/pub/WWW/TheProject.html HTTP/1.1\r\n").uri(),
            Matchers.equalTo(URI.create("http://www.w3.org/pub/WWW/TheProject.html"))
        );
    }

    @Test
    void parsesAbsolutePath() {
        MatcherAssert.assertThat(
            new RequestLineFrom("GET /pub/WWW/TheProject.html HTTP/1.1\r\n").uri(),
            Matchers.equalTo(URI.create("/pub/WWW/TheProject.html"))
        );
    }

    @Test
    void parsesHttpVersion() {
        MatcherAssert.assertThat(
            new RequestLineFrom("PUT * HTTP/1.1\r\n").version(),
            Matchers.equalTo("HTTP/1.1")
        );
    }

    @Test
    void throwsExceptionIfMethodIsUnknown() {
        final String method = "SURRENDER";
        MatcherAssert.assertThat(
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new RequestLineFrom(
                    String.format("%s /wallet/or/life HTTP/1.1\n", method)
                ).method()
            ).getMessage(),
            new IsEqual<>(String.format("Unknown method: '%s'", method))
        );
    }

    @Test
    void throwsExceptionIfLineIsInvalid() {
        final String line = "fake";
        MatcherAssert.assertThat(
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new RequestLineFrom(line).version()
            ).getMessage(),
            new IsEqual<>(String.format("Invalid HTTP request line \n%s", line))
        );
    }
}
