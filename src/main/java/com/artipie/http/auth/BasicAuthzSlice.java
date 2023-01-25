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
public final class BasicAuthzSlice extends Slice.Wrap {

    /**
     * Ctor.
     * @param origin Origin slice
     * @param auth Authorization
     * @param control Access control
     */
    public BasicAuthzSlice(
        final Slice origin, final Authentication auth, final AccessControl control
    ) {
        super(new AuthzSlice(origin, new BasicAuthScheme(auth), control));
    }
}
