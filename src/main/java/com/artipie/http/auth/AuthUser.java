/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.auth;

import java.util.Objects;

/**
 * Authenticated user.
 *
 * @since 0.16
 * @checkstyle DesignForExtensionCheck (500 lines)
 */
public class AuthUser {

    /**
     * User name.
     */
    private final String uname;

    /**
     * Ctor.
     *
     * @param name Name of the user
     */
    public AuthUser(final String name) {
        this.uname = name;
    }

    /**
     * Ger user name.
     *
     * @return Name
     */
    public final String name() {
        return this.uname;
    }

    @Override
    public final boolean equals(final Object other) {
        final boolean res;
        if (this == other) {
            res = true;
        } else if (other == null || this.getClass() != other.getClass()) {
            res = false;
        } else {
            final AuthUser user = (AuthUser) other;
            res = Objects.equals(this.uname, user.uname);
        }
        return res;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.uname);
    }

    @Override
    public String toString() {
        return String.format(
            "%s(%s)",
            this.getClass().getSimpleName(),
            this.uname
        );
    }
}
