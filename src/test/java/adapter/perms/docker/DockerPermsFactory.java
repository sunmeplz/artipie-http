/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package adapter.perms.docker;

import com.artipie.http.perms.ArtipiePermissionFactory;
import com.artipie.http.perms.PermissionConfig;
import com.artipie.http.perms.PermissionFactory;
import java.security.AllPermission;
import java.security.Permission;

/**
 * Test permission.
 * @since 1.2
 */
@ArtipiePermissionFactory("docker-perm")
public final class DockerPermsFactory implements PermissionFactory {
    @Override
    public Permission newPermission(final PermissionConfig config) {
        return new AllPermission();
    }
}
