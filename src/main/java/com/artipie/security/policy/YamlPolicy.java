/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.policy;

import com.artipie.asto.blocking.BlockingStorage;
import java.security.Permissions;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Yaml policy implementation to obtain permissions from yaml files.
 * This implementation DOES NOT CACHE anything, it simply reads and parses permissions
 * on every {@link Policy#getPermissions(String)} method call.
 * The storage itself is expected to have yaml files with permissions in the following structure:
 *
 * ..
 * ├── roles.yaml
 * ├── users
 * │   ├── david.yaml
 * │   ├── jane.yaml
 * │   ├── ...
 *
 * Roles yaml format example:
 * roles:
 *   java-devs:
 *     adapter_basic_permission:
 *       maven-repo:
 *         - read
 *         - write
 *       python-repo:
 *         - read
 *       npm-repo:
 *         - read
 *   admins:
 *     adapter_all_permission:
 *
 * User yaml format example:
 *
 * david:
 *   type: plain
 *   pass: qwerty
 *   email: jane@example.com # Optional
 *   enabled: true # optional default true
 *   roles:
 *     - java-dev
 *   permissions:
 *     artipie_basic_permission:
 *       rpm-repo:
 *         - read
 *
 * @since 1.2
 */
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
public final class YamlPolicy implements Policy<Permissions> {

    /**
     * Storage to read permissions yaml files from.
     */
    private final BlockingStorage asto;

    /**
     * Ctor.
     * @param asto Storage to read permissions yaml files from
     */
    public YamlPolicy(final BlockingStorage asto) {
        this.asto = asto;
    }

    @Override
    public Permissions getPermissions(final String uname) {
        throw new NotImplementedException("To implement");
    }
}
