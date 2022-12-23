/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.auth;

import com.artipie.http.Slice;

/**
 * Slice with basic authentication.
 * @since 1.2
 */
public final class BearerAuthSlice extends Slice.Wrap {

    /**
     * Creates bearer auth slice with {@link BearerAuthScheme} and empty challenge params.
     * @param origin Origin slice
     * @param auth Authorization
     * @param perm Permissions
     */
    public BearerAuthSlice(final Slice origin, final TokenAuthentication auth,
        final Permission perm) {
        super(new AuthSlice(origin, new BearerAuthScheme(auth, ""), perm));
    }

    /**
     * Ctor.
     * @param origin Origin slice
     * @param scheme Bearer authentication scheme
     * @param perm Permissions
     */
    public BearerAuthSlice(final Slice origin, final BearerAuthScheme scheme,
        final Permission perm) {
        super(new AuthSlice(origin, scheme, perm));
    }
}
