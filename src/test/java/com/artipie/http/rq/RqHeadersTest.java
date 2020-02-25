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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.cactoos.iterable.IterableOf;
import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.TextHasString;

/**
 * Test case for {@link RqHeaders}.
 *
 * @since 0.4
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RqHeadersTest {

    @Test
    void findsAllHeaderValues() {
        final String first = "1";
        final String second = "2";
        MatcherAssert.assertThat(
            "RqHeaders didn't find headers by name",
            new RqHeaders(
                new IterableOf<Map.Entry<String, String>>(
                    new MapEntry<>("x-header", first),
                    new MapEntry<>("Accept", "application/json"),
                    new MapEntry<>("X-Header", second)
                ),
                "X-header"
            ),
            Matchers.contains(first, second)
        );
    }

    @Test
    void findSingleValue() {
        final String value = "text/plain";
        MatcherAssert.assertThat(
            "RqHeaders.Single didn't find expected header",
            new RqHeaders.Single(
                Arrays.asList(
                    new MapEntry<>("Content-type", value),
                    new MapEntry<>("Range", "100")
                ),
                "content-type"
            ),
            new TextHasString(value)
        );
    }

    @Test
    void singleFailsIfNoHeadersFound() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new RqHeaders.Single(Collections.emptyList(), "Empty").asString()
        );
    }

    @Test
    void singleFailsIfMoreThanOneHeader() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new RqHeaders.Single(
                Arrays.asList(
                    new MapEntry<>("Content-length", "1024"),
                    new MapEntry<>("Content-Length", "1025")
                ),
                "content-length"
            ).asString()
        );
    }
}
