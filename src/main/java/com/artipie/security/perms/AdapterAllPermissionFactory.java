/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.perms;

import java.security.AllPermission;
import java.security.Permission;

/**
 * Permission factory for {@link AllPermission}.
 * @since 1.2
 */
@ArtipiePermissionFactory("adapter_all_permission")
public final class AdapterAllPermissionFactory implements PermissionFactory {

    @Override
    public Permission newPermission(final PermissionConfig config) {
        return new AllPermission();
    }

}
