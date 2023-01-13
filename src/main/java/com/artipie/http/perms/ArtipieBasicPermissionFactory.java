/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import java.security.Permission;

/**
 * Factory for {@link ArtipieBasicPermission}.
 * @since 1.2
 */
@ArtipiePermissionFactory("artipie_basic_permission")
public final class ArtipieBasicPermissionFactory implements PermissionFactory {

    @Override
    public Permission newPermission(final PermissionConfig config) {
        return new ArtipieBasicPermission(config);
    }

}
