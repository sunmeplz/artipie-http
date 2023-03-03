/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.perms;

import java.security.Permission;

/**
 * Factory for {@link AdapterBasicPermission}.
 * @since 1.2
 */
@ArtipiePermissionFactory("adapter_basic_permission")
public final class AdapterBasicPermissionFactory implements PermissionFactory {

    @Override
    public Permission newPermission(final PermissionConfig config) {
        return new AdapterBasicPermission(config);
    }

}
