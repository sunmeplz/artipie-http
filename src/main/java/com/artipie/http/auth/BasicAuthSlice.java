/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.auth;

import com.artipie.http.Slice;

/**
 * Slice with basic authentication.
 * @since 0.17
 */
public final class BasicAuthSlice extends Slice.Wrap {

    /**
     * Ctor.
     * @param origin Origin slice
     * @param auth Authorization
     * @param perm Permissions
     */
    public BasicAuthSlice(final Slice origin, final Authentication auth, final Permission perm) {
        super(new AuthSlice(origin, new BasicAuthScheme(auth), perm));
    }
}
