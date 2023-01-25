/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.auth;

import com.artipie.security.policy.Policy;
import java.security.Permission;

/**
 * Authorization mechanism with user permission check for slice. The interface is meant to control
 * the access checking if required permission is granted for user.
 * @since 1.2
 */
public interface AccessControl {

    /**
     * Check if user is authorized to perform an action.
     * @param user User name
     * @return True if authorized
     */
    boolean allowed(Authentication.User user);

    /**
     * Access control by required permission.
     * @since 1.2
     */
    final class ByPermission implements AccessControl {

        /**
         * Security policy.
         */
        private final Policy<?> policy;

        /**
         * Required permission.
         */
        private final Permission perm;

        /**
         * Ctor.
         * @param policy Security policy
         * @param perm Required permission
         */
        public ByPermission(final Policy<?> policy, final Permission perm) {
            this.policy = policy;
            this.perm = perm;
        }

        @Override
        public boolean allowed(final Authentication.User user) {
            return this.policy.getPermissions(user.name()).implies(this.perm);
        }
    }
}
