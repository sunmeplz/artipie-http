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

import com.artipie.http.rq.RqHeaders;
import java.util.Map;
import java.util.Optional;
import org.cactoos.text.Base64Decoded;

/**
 * BasicIdentities. Implementation of {@link Identities} for Basic authorization.
 *
 * @since 0.8
 * @checkstyle NestedIfDepthCheck (500 lines)
 */
public final class BasicIdentities implements Identities {

    /**
     * Concrete implementation for User Identification.
     */
    private final Authentication auth;

    /**
     * Ctor.
     * @param auth Concrete implementation for User Identification.
     */
    public BasicIdentities(final Authentication auth) {
        this.auth = auth;
    }

    @Override
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    public Optional<String> user(final String line,
        final Iterable<Map.Entry<String, String>> headers) {
        final String prefix = "Basic ";
        Optional<String> result = Optional.empty();
        final RqHeaders hdrs = new RqHeaders(headers, "Authorization");
        if (!hdrs.isEmpty()) {
            final String cred = hdrs.get(0);
            if (cred.startsWith(prefix)) {
                final String[] credentials =
                    new Base64Decoded(cred.substring(prefix.length())).toString().split(":");
                final String username = credentials[0].trim();
                final String password = credentials[1].trim();
                this.auth.verify(username, password);
                result = Optional.of(username);
            }
        }
        return result;
    }
}
