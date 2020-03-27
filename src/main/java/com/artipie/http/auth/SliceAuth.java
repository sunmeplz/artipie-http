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

import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.rs.RsStatus;
import com.artipie.http.rs.RsWithStatus;
import java.nio.ByteBuffer;
import java.util.Map;
import org.reactivestreams.Publisher;

/**
 * Slice wrapper with authorization and authentication.
 * <p>
 * Example: the class which allows upload only for users with 'upload' permissions
 * and resolves user identity by "http-basic" mechanism:
 * <pre><code>
 * new SliceAuth(
 *   new SliceUpload(storage),
 *   new SliceAuth.Authorization(permissions, "upload"),
 *   new AuthBasic(passwords)
 * );
 * </code></pre>
 * </p>
 * @since 0.8
 */
public final class SliceAuth implements Slice {

    /**
     * Origin slice.
     */
    private final Slice origin;

    /**
     * Authorization.
     */
    private final Permission perm;

    /**
     * Authentication.
     */
    private final Identities ids;

    /**
     * Ctor.
     * @param origin Origin slice
     * @param perm Authorization mechanism
     * @param ids Authentication mechanism
     */
    public SliceAuth(final Slice origin, final Permission perm, final Identities ids) {
        this.origin = origin;
        this.ids = ids;
        this.perm = perm;
    }

    @Override
    public Response response(final String line,
        final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        return this.ids.user(line, headers)
            .map(this.perm::allowed).map(
                allowed -> {
                    final Response rsp;
                    if (allowed) {
                        rsp = this.origin.response(line, headers, body);
                    } else {
                        rsp = new RsWithStatus(RsStatus.FORBIDDEN);
                    }
                    return rsp;
                }
            ).orElse(new RsWithStatus(RsStatus.UNAUTHORIZED));
    }

    /**
     * Authorization mechanism with single permission check for slice.
     * @since 0.8
     */
    public static final class Permission {

        /**
         * All permissions.
         */
        private final Permissions perm;

        /**
         * Action to perform.
         */
        private final String action;

        /**
         * Ctor.
         * @param perm Permissions
         * @param action Action
         */
        public Permission(final Permissions perm, final String action) {
            this.perm = perm;
            this.action = action;
        }

        /**
         * Check if user is authorized to perform an action.
         * @param user User name
         * @return True if authorized
         */
        public boolean allowed(final String user) {
            return this.perm.allowed(user, this.action);
        }
    }
}
