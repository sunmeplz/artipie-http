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
package com.artipie.http.auth;

import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test for {@link Permission.ByName}.
 * @since 0.16
 */
class PermissionByNameTest {

    @ParameterizedTest
    @CsvSource({
        "READ,download,john",
        "READ,r,john",
        "READ,install,john",
        "WRITE,w,alice",
        "WRITE,write,alice",
        "WRITE,upload,alice",
        "WRITE,publish,alice",
        "WRITE,push,alice",
        "WRITE,deploy,mark",
        "DELETE,delete,mark",
        "DELETE,d,mark"
    })
    void checksAllActions(final Action.Standard action, final String perm, final String user) {
        MatcherAssert.assertThat(
            new Permission.ByName(
                (name, act) -> user.equals(name) && act.equals(perm),
                action
            ).allowed(user),
            new IsEqual<>(true)
        );
    }

    @Test
    void doesNotAllowWhenActionIsNotPresent() {
        final String user = "jane";
        MatcherAssert.assertThat(
            new Permission.ByName(
                (name, act) -> user.equals(name) && act.equals("a"),
                () -> Arrays.asList("b", "c")
            ).allowed(user),
            new IsEqual<>(false)
        );
    }

}
