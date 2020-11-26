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

/**
 * Basic authentication method.
 *
 * @since 0.17
 */
public final class BasicAuthScheme implements AuthScheme {

    /**
     * Basic authentication prefix.
     */
    public static final String NAME = "Basic";

    /**
     * Authentication.
     */
    private final Authentication auth;

    /**
     * Ctor.
     * @param auth Authentication.
     */
    public BasicAuthScheme(final Authentication auth) {
        this.auth = auth;
    }

    @Override
    public Result authenticate(final Iterable<Map.Entry<String, String>> headers) {
        return this.user(headers).<Result>map(Success::new).orElseGet(Failure::new);
    }

    /**
     * Obtains user from authentication header.
     * @param headers Headers
     * @return User if authorised
     */
    private Optional<Authentication.User> user(final Iterable<Map.Entry<String, String>> headers) {
        return new RqHeaders(headers, Authorization.NAME).stream()
            .findFirst()
            .map(Authorization::new)
            .filter(hdr -> hdr.scheme().equals(BasicAuthScheme.NAME))
            .map(hdr -> new Authorization.Basic(hdr.credentials()))
            .flatMap(hdr -> this.auth.user(hdr.username(), hdr.password()));
    }

    /**
     * Successful result with authenticated user.
     *
     * @since 0.17
     */
    private static class Success implements Result {

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
            return BasicAuthScheme.NAME;
        }
    }

    /**
     * Failed result without authenticated user.
     *
     * @since 0.17
     */
    private static class Failure implements Result {

        @Override
        public Optional<Authentication.User> user() {
            return Optional.empty();
        }

        @Override
        public String challenge() {
            return BasicAuthScheme.NAME;
        }
    }
}
