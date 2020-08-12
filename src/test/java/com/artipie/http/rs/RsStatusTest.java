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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link RsStatus}.
 *
 * @since 0.16
 */
final class RsStatusTest {
    @Test
    void createsClientError() {
        final RsStatus status = RsStatus.BAD_REQUEST;
        Assertions.assertTrue(status.isClientError());
        Assertions.assertTrue(status.isError());
        Assertions.assertFalse(status.isServerError());
    }

    @Test
    void createsServerError() {
        final RsStatus status = RsStatus.INTERNAL_ERROR;
        Assertions.assertFalse(status.isClientError());
        Assertions.assertTrue(status.isError());
        Assertions.assertTrue(status.isServerError());
    }

    @Test
    void createsNotError() {
        final RsStatus success = RsStatus.OK;
        final RsStatus rscontinue = RsStatus.CONTINUE;
        final RsStatus found = RsStatus.FOUND;
        Assertions.assertFalse(success.isError());
        Assertions.assertFalse(rscontinue.isError());
        Assertions.assertFalse(found.isError());
    }

}
