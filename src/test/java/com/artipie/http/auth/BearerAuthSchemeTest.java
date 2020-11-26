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

import com.artipie.http.Headers;
import com.artipie.http.headers.Authorization;
import com.artipie.http.headers.Header;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test for {@link BearerAuthScheme}.
 *
 * @since 0.17
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class BearerAuthSchemeTest {

    @Test
    void shouldExtractTokenFromHeaders() {
        final String token = "12345";
        final AtomicReference<String> capture = new AtomicReference<>();
        new BearerAuthScheme(
            tkn -> {
                capture.set(tkn);
                return Optional.of(new Authentication.User("alice"));
            },
            "realm=\"artipie.com\""
        ).authenticate(new Headers.From(new Authorization.Bearer(token)));
        MatcherAssert.assertThat(
            capture.get(),
            new IsEqual<>(token)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"bob"})
    @NullSource
    void shouldReturnUserInResult(final String name) {
        final Optional<Authentication.User> user = Optional.ofNullable(name)
            .map(Authentication.User::new);
        final Optional<Authentication.User> result = new BearerAuthScheme(
            tkn -> user,
            "whatever"
        ).authenticate(new Headers.From(new Authorization.Bearer("abc"))).user();
        MatcherAssert.assertThat(result, new IsEqual<>(user));
    }

    @ParameterizedTest
    @MethodSource("badHeaders")
    void shouldNotAuthWhenNoAuthHeader(final Headers headers) {
        final String params = "realm=\"artipie.com/auth\",param1=\"123\"";
        final AuthScheme.Result result = new BearerAuthScheme(
            tkn -> Optional.empty(),
            params
        ).authenticate(headers);
        MatcherAssert.assertThat(
            "Not authenticated",
            result.user().isPresent(),
            new IsEqual<>(false)
        );
        MatcherAssert.assertThat(
            "Has expected challenge",
            result.challenge(),
            new IsEqual<>(String.format("Bearer %s", params))
        );
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Headers> badHeaders() {
        return Stream.of(
            new Headers.From(),
            new Headers.From(new Header("X-Something", "some value")),
            new Headers.From(new Authorization.Basic("charlie", "qwerty"))
        );
    }
}
