/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.auth;

import java.util.Map;
import java.util.Optional;

/**
 * Authentication decoder, read the user name from request head.
 * <p>
 * Possible implementations are Basic, etc.
 * </p>
 * @since 0.8
 * @deprecated We do not use this interface anymore, use {@link Authentication} instead
 */
@Deprecated
public interface Identities {

    /**
     * Resolve any request as anonymous user.
     */
    Identities ANONYMOUS = (line, headers) -> Optional.of(new AuthUser("anonymous", "unknown"));

    /**
     * Try to find a user by request head.
     * @param line Request line
     * @param headers Request headers
     * @return User name if found
     */
    Optional<AuthUser> user(String line, Iterable<Map.Entry<String, String>> headers);
}
