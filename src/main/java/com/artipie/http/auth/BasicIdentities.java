/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.auth;

import com.artipie.http.headers.Authorization;
import com.artipie.http.rq.RqHeaders;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

/**
 * BasicIdentities. Implementation of {@link Identities} for Basic authorization.
 *
 * @since 0.8
 * @checkstyle NestedIfDepthCheck (500 lines)
 * @deprecated Use {@link BasicAuthSlice} instead
 */
@Deprecated
public final class BasicIdentities implements Identities {

    /**
     * Basic authentication prefix.
     */
    private static final String PREFIX = "Basic ";

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
    public Optional<Authentication.User> user(final String line,
        final Iterable<Map.Entry<String, String>> headers) {
        return new RqHeaders(headers, Authorization.NAME).stream()
            .findFirst()
            .filter(hdr -> hdr.startsWith(BasicIdentities.PREFIX))
            .map(hdr -> Base64.getDecoder().decode(hdr.substring(BasicIdentities.PREFIX.length())))
            .map(bts -> new String(bts, StandardCharsets.UTF_8))
            .map(dec -> dec.toString().split(":"))
            .flatMap(
                cred -> this.auth.user(cred[0].trim(), cred[1].trim())
            );
    }
}
