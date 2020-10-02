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

import com.artipie.asto.Content;
import com.artipie.http.Headers;
import com.artipie.http.headers.Authorization;
import com.artipie.http.headers.Header;
import com.artipie.http.hm.RsHasHeaders;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.hm.SliceHasResponse;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rs.RsStatus;
import com.artipie.http.rs.RsWithStatus;
import com.artipie.http.rs.StandardRs;
import com.artipie.http.slice.SliceSimple;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link BasicAuthSlice}.
 * @since 0.17
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class BasicAuthSliceTest {

    @Test
    void proxyToOriginSliceIfAllowed() {
        MatcherAssert.assertThat(
            new BasicAuthSlice(
                new SliceSimple(StandardRs.OK),
                (user, pswd) -> Optional.of(new Authentication.User("someone")),
                user -> true
            ),
            new SliceHasResponse(
                new RsHasStatus(RsStatus.OK),
                new RequestLine("GET", "/foo")
            )
        );
    }

    @Test
    void returnsUnauthorizedErrorIfUnableToAuthenticate() {
        MatcherAssert.assertThat(
            new BasicAuthSlice(
                new SliceSimple(StandardRs.OK),
                (user, pswd) -> Optional.empty(),
                user -> false
            ),
            new SliceHasResponse(
                Matchers.allOf(
                    new RsHasStatus(RsStatus.UNAUTHORIZED),
                    new RsHasHeaders(new Header("WWW-Authenticate", "Basic"))
                ),
                new RequestLine("POST", "/bar", "HTTP/1.2")
            )
        );
    }

    @Test
    void returnsForbiddenIfNotAllowed() {
        final String name = "john";
        MatcherAssert.assertThat(
            new BasicAuthSlice(
                new SliceSimple(new RsWithStatus(RsStatus.OK)),
                (user, pswd) -> Optional.of(new Authentication.User(name)),
                new Permission.ByName("read", (user, action) -> false)
            ),
            new SliceHasResponse(
                new RsHasStatus(RsStatus.FORBIDDEN),
                new RequestLine("DELETE", "/baz", "HTTP/1.3"),
                new Headers.From(new Authorization.Basic(name, "123")),
                Content.EMPTY
            )
        );
    }

}
