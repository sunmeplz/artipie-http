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

import com.artipie.http.Headers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link ContentDisposition}.
 *
 * @since 0.17.8
 */
public final class ContentDispositionTest {

    @Test
    void shouldHaveExpectedValue() {
        MatcherAssert.assertThat(
            new ContentDisposition("").getKey(),
            new IsEqual<>("Content-Disposition")
        );
    }

    @Test
    void shouldExtractFileName() {
        MatcherAssert.assertThat(
            new ContentDisposition(
                new Headers.From(
                    new Header("Content-Type", "application/octet-stream"),
                    new Header("content-disposition", "attachment; filename=\"filename.jpg\"")
                )
            ).fileName(),
            new IsEqual<>("filename.jpg")
        );
    }

    @Test
    void shouldFailToExtractLongValueFromEmptyHeaders() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new ContentDisposition(Headers.EMPTY).fileName()
        );
    }

    @Test
    void shouldBeInline() {
        MatcherAssert.assertThat(
            new ContentDisposition("inline").isInline(),
            new IsTrue()
        );
    }

    @Test
    void shouldBeAttachment() {
        MatcherAssert.assertThat(
            new ContentDisposition("attachment; name=\"input\"").isAttachment(),
            new IsTrue()
        );
    }

    @Test
    void parsesNameDirective() {
        MatcherAssert.assertThat(
            new ContentDisposition("attachment; name=\"field\"").fieldName(),
            new IsEqual<>("field")
        );
    }

    @Test
    void parsesFilenameDirective() {
        MatcherAssert.assertThat(
            new ContentDisposition("attachment; filename=\"foo.jpg\"").fileName(),
            new IsEqual<>("foo.jpg")
        );
    }
}
