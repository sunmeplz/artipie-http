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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BytesAreString}.
 *
 * @since 0.7
 */
class BytesAreStringTest {

    @Test
    void shouldMatchEqualString() {
        final Charset charset = StandardCharsets.UTF_8;
        final String string = "\u00F6";
        final BytesAreString matcher = new BytesAreString(
            charset,
            new StringContains(false, string)
        );
        MatcherAssert.assertThat(
            matcher.matches(string.getBytes(charset)),
            new IsEqual<>(true)
        );
    }

    @Test
    void shouldNotMatchNotEqualString() {
        MatcherAssert.assertThat(
            new BytesAreString("1").matches("2".getBytes()),
            new IsEqual<>(false)
        );
    }
}
