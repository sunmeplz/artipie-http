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
import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.headers.Authorization;
import com.artipie.http.headers.WwwAuthenticate;
import com.artipie.http.rq.RqHeaders;
import com.artipie.http.rs.RsStatus;
import com.artipie.http.rs.RsWithHeaders;
import com.artipie.http.rs.RsWithStatus;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import org.cactoos.text.Base64Decoded;
import org.reactivestreams.Publisher;

/**
 * Slice with basic authentication.
 * @since 0.17
 */
public final class BasicAuthSlice implements Slice {

    /**
     * Basic authentication prefix.
     */
    public static final String SCHEME = "Basic";

    /**
     * Origin.
     */
    private final Slice origin;

    /**
     * Authorization.
     */
    private final Authentication auth;

    /**
     * Permissions.
     */
    private final Permission perm;

    /**
     * Ctor.
     * @param origin Origin slice
     * @param auth Authorization
     * @param perm Permissions
     */
    public BasicAuthSlice(final Slice origin, final Authentication auth, final Permission perm) {
        this.origin = origin;
        this.auth = auth;
        this.perm = perm;
    }

    @Override
    public Response response(final String line, final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        final Response result;
        if (this.perm.allowed(Permissions.ANY_USER)) {
            result = this.origin.response(line, headers, body);
        } else {
            result = this.user(headers).map(this.perm::allowed).map(
                allowed -> {
                    final Response rsp;
                    if (allowed) {
                        rsp = this.origin.response(line, headers, body);
                    } else {
                        rsp = new RsWithStatus(RsStatus.FORBIDDEN);
                    }
                    return rsp;
                }
            ).orElseGet(
                () -> new RsWithHeaders(
                    new RsWithStatus(RsStatus.UNAUTHORIZED),
                    new Headers.From(new WwwAuthenticate(BasicAuthSlice.SCHEME))
                )
            );
        }
        return result;
    }

    /**
     * Obtains user from authentication header.
     * @param headers Headers
     * @return User if authorised
     */
    private Optional<Authentication.User> user(final Iterable<Map.Entry<String, String>> headers) {
        return new RqHeaders(headers, Authorization.NAME).stream()
            .findFirst()
            .filter(hdr -> hdr.startsWith(BasicAuthSlice.SCHEME))
            .map(hdr -> new Base64Decoded(hdr.substring(BasicAuthSlice.SCHEME.length() + 1)))
            .map(dec -> dec.toString().split(":"))
            .flatMap(cred -> this.auth.user(cred[0].trim(), cred[1].trim()));
    }
}
