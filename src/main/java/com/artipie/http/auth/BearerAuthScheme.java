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

import com.artipie.http.headers.Authorization;
import com.artipie.http.rq.RqHeaders;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Basic authentication method.
 *
 * @since 0.17
 */
public final class BearerAuthScheme implements AuthScheme {

    /**
     * Basic authentication prefix.
     */
    public static final String NAME = "Bearer";

    /**
     * Authentication.
     */
    private final TokenAuthentication auth;

    /**
     * Challenge parameters.
     */
    private final String params;

    /**
     * Ctor.
     *
     * @param auth Authentication.
     * @param params Challenge parameters.
     */
    public BearerAuthScheme(final TokenAuthentication auth, final String params) {
        this.auth = auth;
        this.params = params;
    }

    @Override
    public CompletionStage<Result> authenticate(final Iterable<Map.Entry<String, String>> headers) {
        return this.user(headers).thenApply(
            user -> user.<Result>map(Success::new).orElseGet(Failure::new)
        );
    }

    /**
     * Obtains user from authentication header.
     *
     * @param headers Headers
     * @return User, empty if not authenticated
     */
    private CompletionStage<Optional<Authentication.User>> user(
        final Iterable<Map.Entry<String, String>> headers
    ) {
        return new RqHeaders(headers, Authorization.NAME).stream()
            .findFirst()
            .map(Authorization::new)
            .filter(hdr -> hdr.scheme().equals(BearerAuthScheme.NAME))
            .map(hdr -> new Authorization.Bearer(hdr.credentials()).token())
            .map(this.auth::user)
            .orElseGet(() -> CompletableFuture.completedFuture(Optional.empty()));
    }

    /**
     * Challenge for client to be provided as WWW-Authenticate header value.
     *
     * @return Challenge string.
     */
    private String challenge() {
        return String.format("%s %s", BearerAuthScheme.NAME, this.params);
    }

    /**
     * Successful result with authenticated user.
     *
     * @since 0.17
     */
    private class Success implements Result {

        /**
         * Authenticated user.
         */
        private final Authentication.User usr;

        /**
         * Ctor.
         *
         * @param user Authenticated user.
         */
        Success(final Authentication.User user) {
            this.usr = user;
        }

        @Override
        public Optional<Authentication.User> user() {
            return Optional.of(this.usr);
        }

        @Override
        public String challenge() {
            return BearerAuthScheme.this.challenge();
        }
    }

    /**
     * Failed result without authenticated user.
     *
     * @since 0.17
     */
    private class Failure implements Result {

        @Override
        public Optional<Authentication.User> user() {
            return Optional.empty();
        }

        @Override
        public String challenge() {
            return BearerAuthScheme.this.challenge();
        }
    }
}
