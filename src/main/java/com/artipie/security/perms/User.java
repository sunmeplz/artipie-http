/*
 * The MIT License (MIT) Copyright (c) 2020-2023 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.perms;

import java.security.PermissionCollection;
import java.util.Collection;

/**
 * User provides its individual permission collection and
 * groups.
 * @since 1.2
 */
public interface User {

    /**
     * Returns user groups.
     * @return Collection of the groups
     */
    Collection<String> roles();

    /**
     * Returns user's individual permissions.
     * @return Individual permissions collection
     */
    PermissionCollection perms();

    /**
     * Simple user accepts user's groups and permissions in the ctor and
     * returns them in the {@link User#roles()} and {@link User#perms()} methods.
     * @since 1.2
     */
    final class Simple implements User {

        /**
         * User groups.
         */
        private final Collection<String> groups;

        /**
         * User permissions.
         */
        private final PermissionCollection permissions;

        /**
         * Ctor.
         * @param groups User groups
         * @param permissions User permissions
         */
        public Simple(
            final Collection<String> groups,
            final PermissionCollection permissions
        ) {
            this.groups = groups;
            this.permissions = permissions;
        }

        @Override
        public Collection<String> roles() {
            return this.groups;
        }

        @Override
        public PermissionCollection perms() {
            return this.permissions;
        }
    }
}
