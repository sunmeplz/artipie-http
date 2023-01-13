/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import java.security.AllPermission;
import java.security.Permission;

/**
 * Permission factory for {@link AllPermission}.
 * @since 1.2
 */
@ArtipiePermissionFactory("artipie_all_permission")
public final class ArtipieAllPermissionFactory implements PermissionFactory {

    @Override
    public Permission newPermission(final PermissionConfig config) {
        return new AllPermission();
    }

}
