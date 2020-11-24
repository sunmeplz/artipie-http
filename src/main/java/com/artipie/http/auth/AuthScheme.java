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

import java.util.Map;
import java.util.Optional;

/**
 * Authentication scheme such as Basic, Bearer etc.
 *
 * @since 0.17
 */
public interface AuthScheme {

    /**
     * Authenticate HTTP request by it's headers.
     *
     * @param headers Request headers.
     * @return Authentication result.
     */
    Result authenticate(Iterable<Map.Entry<String, String>> headers);

    /**
     * HTTP request authentication result.
     *
     * @since 0.17
     */
    interface Result {

        /**
         * Authenticated user.
         *
         * @return Authenticated user, empty if not authenticated.
         */
        Optional<Authentication.User> user();

        /**
         * Get authentication challenge that is provided in response WWW-Authenticate header value.
         *
         * @return Authentication challenge for client.
         */
        String challenge();
    }
}
