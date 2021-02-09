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
package com.artipie.http.headers;

import java.net.URI;
import java.net.URISyntaxException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ContentFileName}.
 *
 * @since 0.17.8
 */
final class ContentFileNameTest {

    @Test
    void shouldBeContentDispositionHeader() {
        MatcherAssert.assertThat(
            new ContentFileName("bar.txt").getKey(),
            new IsEqual<>("Content-Disposition")
        );
    }

    @Test
    void shouldHaveQuotedValue() {
        MatcherAssert.assertThat(
            new ContentFileName("foo.txt").getValue(),
            new IsEqual<>("attachment; filename=\"foo.txt\"")
        );
    }

    @Test
    void shouldTakeUriAsParameter() throws URISyntaxException {
        MatcherAssert.assertThat(
            new ContentFileName(
                new URI("https://example.com/index.html")
            ).getValue(),
            new IsEqual<>("attachment; filename=\"index.html\"")
        );
    }
}
