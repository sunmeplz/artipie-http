/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.perms;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of {@link PermissionCollection} for user. It takes into account
 * user personal permissions and his roles. The roles are stored as collection,
 * user permissions are represented by {@link Supplier} interface in order they can actually be
 * addressed on demand only. The same goes for permissions for role, they are represented by
 * {@link Function} interface and are addressed only on demand.
 * <p>
 * The implementations of the {@link Supplier} and {@link Function} are user's choose, it can
 * read permissions from file on each call or use some complex caches inside.
 *
 * @since 1.2
 */
public final class UserPermissions extends PermissionCollection {

    /**
     * Required serial.
     */
    private static final long serialVersionUID = -7546496571951236695L;

    /**
     * Role permissions.
     */
    private final Function<String, PermissionCollection> rperms;

    /**
     * User roles.
     */
    private final Collection<String> roles;

    /**
     * User permissions.
     */
    private final Supplier<PermissionCollection> perms;

    /**
     * The name of the group, which implied the permission in the previous
     * {@link UserPermissions#implies(Permission)} method call. Empty if
     * user permissions implied the permission.
     */
    private final AtomicReference<String> last;

    /**
     * Ctor.
     * @param perms User permissions
     * @param roles User roles
     * @param rperms Role permissions
     */
    public UserPermissions(
        final Supplier<PermissionCollection> perms,
        final Collection<String> roles,
        final Function<String, PermissionCollection> rperms
    ) {
        this.rperms = rperms;
        this.roles = roles;
        this.perms = perms;
        this.last = new AtomicReference<>();
    }

    @Override
    public void add(final Permission permission) {
        this.perms.get().add(permission);
    }

    @Override
    public boolean implies(final Permission permission) {
        boolean res;
        final String ref = this.last.get();
        if (ref == null) {
            res = this.perms.get().implies(permission);
        } else {
            res = this.rperms.apply(ref).implies(permission);
        }
        if (!res) {
            if (ref != null) {
                res = this.perms.get().implies(permission);
            }
            if (res) {
                this.last.set(null);
            } else {
                // @checkstyle NestedIfDepthCheck (5 lines)
                for (final String role : this.roles) {
                    if (!role.equals(ref) && this.rperms.apply(role).implies(permission)) {
                        res = true;
                        this.last.set(role);
                        break;
                    }
                }
            }
        }
        return res;
    }

    @Override
    public Enumeration<Permission> elements() {
        return this.perms.get().elements();
    }
}
