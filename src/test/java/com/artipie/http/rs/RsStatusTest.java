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
package com.artipie.http.rs;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Test for {@link RsStatus}.
 *
 * @since 0.16
 */
final class RsStatusTest {
    @Test
    void information() {
        final RsStatus status = RsStatus.CONTINUE;
        MatcherAssert.assertThat(
            status.information(),
            new IsEqual<>(true)
        );
    }

    @Test
    void success() {
        final RsStatus status = RsStatus.ACCEPTED;
        MatcherAssert.assertThat(
            status.success(),
            new IsEqual<>(true)
        );
    }

    @Test
    void redirection() {
        final RsStatus status = RsStatus.FOUND;
        MatcherAssert.assertThat(
            status.redirection(),
            new IsEqual<>(true)
        );
    }

    @Test
    void clientError() {
        final RsStatus status = RsStatus.BAD_REQUEST;
        MatcherAssert.assertThat(
            status.clientError(),
            new IsEqual<>(true)
        );
    }

    @Test
    void serverError() {
        final RsStatus status = RsStatus.INTERNAL_ERROR;
        MatcherAssert.assertThat(
            status.serverError(),
            new IsEqual<>(true)
        );
    }

    @ParameterizedTest
    @EnumSource(value = RsStatus.class, names = {"FORBIDDEN", "INTERNAL_ERROR"})
    void error(final RsStatus status) {
        MatcherAssert.assertThat(
            status.error(),
            new IsEqual<>(true)
        );
    }

    @ParameterizedTest
    @EnumSource(value = RsStatus.class, names = {"CONTINUE", "OK", "FOUND"})
    void notError(final RsStatus status) {
        MatcherAssert.assertThat(
            status.error(),
            new IsEqual<>(false)
        );
    }
}
