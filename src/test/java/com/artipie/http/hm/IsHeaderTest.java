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
package com.artipie.http.hm;

import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link IsHeader}.
 *
 * @since 0.8
 */
class IsHeaderTest {

    @Test
    void shouldMatchEqual() {
        final String name = "Content-Length";
        final String value = "100";
        final IsHeader matcher = new IsHeader(name, value);
        MatcherAssert.assertThat(
            matcher.matches(new MapEntry<>(name, value)),
            new IsEqual<>(true)
        );
    }

    @Test
    void shouldMatchUsingValueMatcher() {
        final IsHeader matcher = new IsHeader(
            "content-type", new StringStartsWith(false, "text/plain")
        );
        MatcherAssert.assertThat(
            matcher.matches(
                new MapEntry<>("Content-Type", "text/plain; charset=us-ascii")
            ),
            new IsEqual<>(true)
        );
    }

    @Test
    void shouldNotMatchNotEqual() {
        MatcherAssert.assertThat(
            new IsHeader("name", "value").matches(new MapEntry<>("n", "v")),
            new IsEqual<>(false)
        );
    }
}
