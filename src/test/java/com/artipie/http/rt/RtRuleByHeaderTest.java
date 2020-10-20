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
package com.artipie.http.rt;

import com.artipie.http.Headers;
import java.util.regex.Pattern;
import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link RtRule.ByHeader}.
 * @since 0.17
 */
class RtRuleByHeaderTest {

    @Test
    void trueIfHeaderIsPresent() {
        final String name = "some header";
        MatcherAssert.assertThat(
            new RtRule.ByHeader(name).apply(
                "what ever", new Headers.From(new MapEntry<>(name, "any value"))
            ),
            new IsEqual<>(true)
        );
    }

    @Test
    void falseIfHeaderIsNotPresent() {
        MatcherAssert.assertThat(
            new RtRule.ByHeader("my header").apply("rq line", Headers.EMPTY),
            new IsEqual<>(false)
        );
    }

    @Test
    void trueIfHeaderIsPresentAndValueMatchesRegex() {
        final String name = "content-type";
        MatcherAssert.assertThat(
            new RtRule.ByHeader(name, Pattern.compile("text/html.*")).apply(
                "/some/path", new Headers.From(new MapEntry<>(name, "text/html; charset=utf-8"))
            ),
            new IsEqual<>(true)
        );
    }

    @Test
    void falseIfHeaderIsPresentAndValueDoesNotMatchesRegex() {
        final String name = "Accept-Encoding";
        MatcherAssert.assertThat(
            new RtRule.ByHeader(name, Pattern.compile("gzip.*")).apply(
                "/another/path", new Headers.From(new MapEntry<>(name, "deflate"))
            ),
            new IsEqual<>(false)
        );
    }

}
