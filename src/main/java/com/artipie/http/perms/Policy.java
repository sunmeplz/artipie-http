/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import java.security.PermissionCollection;

/**
 * Security policy.
 *
 * @param <C> Implementation of {@link PermissionCollection}
 * @since 1.2
 */
public interface Policy<C extends PermissionCollection> {

    /**
     * Get collection of permissions {@link PermissionCollection} for user by username.
     * <p>
     * Each user can have permissions of various types, for example:
     * list of {@link AdapterBasicPermission} for adapter with basic permissions and
     * another permissions' implementation for docker adapter.
     *
     * @param uname User name
     * @return Set of {@link PermissionCollection}
     */
    C getPermissions(String uname);

}
