/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.policy;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlSequence;
import com.artipie.asto.Key;
import com.artipie.asto.ValueNotFoundException;
import com.artipie.asto.blocking.BlockingStorage;
import com.artipie.security.perms.EmptyPermissions;
import com.artipie.security.perms.PermissionConfig;
import com.artipie.security.perms.PermissionsLoader;
import com.artipie.security.perms.UserPermissions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Yaml policy implementation to obtain permissions from yaml files.
 * This implementation DOES NOT CACHE anything, it simply reads and parses permissions
 * on every {@link Policy#getPermissions(String)} method call.
 * The storage itself is expected to have yaml files with permissions in the following structure:
 * <pre>
 * ..
 * ├── roles
 * │   ├── java-dev.yaml
 * │   ├── admin.yaml
 * │   ├── ...
 * ├── users
 * │   ├── david.yaml
 * │   ├── jane.yaml
 * │   ├── ...
 * </pre>
 * Roles yaml file name is the name of the group, format example:
 * <pre>{@code
 * java-dev:
 *   permissions:
 *     adapter_basic_permission:
 *       maven-repo:
 *         - read
 *         - write
 *       python-repo:
 *         - read
 *       npm-repo:
 *         - read
 * }</pre>
 * Or for admin:
 * <pre>{@code
 * admin:
 *   enabled: true # optional default true
 *   permissions:
 *     adapter_all_permission: {}
 * }</pre>
 * Role can be disabled with the help of optional {@code enabled} field.
 * <p>User yaml format example:
 * <pre>{@code
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
 * }</pre>
 * @since 1.2
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class YamlPolicy implements Policy<UserPermissions> {

    /**
     * Permissions factories.
     */
    private static final PermissionsLoader FACTORIES = new PermissionsLoader();

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
    public UserPermissions getPermissions(final String uname) {
        final User urs = new User(this.asto, uname);
        return new UserPermissions(urs.perms(), urs.roles(), new Roles(this.asto));
    }

    /**
     * Read yaml file from storage considering both yaml and yml extensions. If nighter
     * version exists, exception is thrown.
     * @param asto Blocking storage
     * @param filename The name of the file
     * @return The value in bytes
     * @throws ValueNotFoundException If file not found
     * @throws UncheckedIOException If yaml parsing failed
     */
    private static YamlMapping readFile(final BlockingStorage asto, final String filename) {
        final byte[] res;
        final Key yaml = new Key.From(String.format("%s.yaml", filename));
        final Key yml = new Key.From(String.format("%s.yml", filename));
        if (asto.exists(yaml)) {
            res = asto.value(yaml);
        } else if (asto.exists(yml)) {
            res = asto.value(yml);
        } else {
            throw new ValueNotFoundException(yaml);
        }
        try {
            return Yaml.createYamlInput(new ByteArrayInputStream(res)).readYamlMapping();
        } catch (final IOException err) {
            throw new UncheckedIOException(err);
        }
    }

    /**
     * Read and instantiate permissions from yaml mapping.
     * @param mapping Yaml mapping
     * @return Permissions set
     */
    private static PermissionCollection readPermissionsFromYaml(final YamlMapping mapping) {
        final YamlMapping all = mapping.yamlMapping("permissions");
        final PermissionCollection res;
        if (all == null || all.keys().isEmpty()) {
            res = new EmptyPermissions();
        } else {
            res = new Permissions();
            for (final String type : all.keys().stream().map(item -> item.asScalar().value())
                .collect(Collectors.toSet())) {
                final YamlMapping perms = all.yamlMapping(type);
                if (perms == null || perms.keys().isEmpty()) {
                    res.add(
                        FACTORIES.newPermission(
                            type, new PermissionConfig.Yaml(Yaml.createYamlMappingBuilder().build())
                        )
                    );
                } else {
                    perms.keys().stream().map(key -> key.asScalar().value()).forEach(
                        key -> res.add(
                            FACTORIES.newPermission(
                                type,
                                new PermissionConfig.Yaml(
                                    Yaml.createYamlMappingBuilder().add(
                                        key, perms.asMapping().yamlSequence(key)
                                    ).build()
                                )
                            )
                        )
                    );
                }
            }
        }
        return res;
    }

    /**
     * Groups from storage.
     * @since 1.2
     */
    public static final class Roles implements Function<String, PermissionCollection> {

        /**
         * Enable yaml field.
         */
        private static final String ENABLED = "enabled";

        /**
         * Storage to read roles yaml file from.
         */
        private final BlockingStorage asto;

        /**
         * Ctor.
         * @param asto Storage to read roles yaml file from
         */
        Roles(final BlockingStorage asto) {
            this.asto = asto;
        }

        @Override
        public PermissionCollection apply(final String role) {
            final YamlMapping mapping = YamlPolicy.readFile(
                this.asto, String.format("roles/%s", role)
            ).yamlMapping(role);
            final String enabled = mapping.string(Roles.ENABLED);
            final PermissionCollection res;
            if (Boolean.FALSE.toString().equalsIgnoreCase(enabled)) {
                res = new EmptyPermissions();
            } else {
                res = YamlPolicy.readPermissionsFromYaml(mapping);
            }
            return res;
        }

    }

    /**
     * User from storage.
     * @since 1.2
     */
    static final class User {

        /**
         * String to format user settings file name.
         */
        private static final String FORMAT = "users/%s";

        /**
         * User info yaml mapping.
         */
        private final YamlMapping yaml;

        /**
         * Ctor.
         * @param asto Storage to read user yaml file from
         * @param username The name of the user
         */
        User(final BlockingStorage asto, final String username) {
            this.yaml = YamlPolicy.readFile(asto, String.format(User.FORMAT, username))
                .yamlMapping(username);
        }

        /**
         * Is user enabled?
         * @return True is user is active
         */
        boolean enabled() {
            return Boolean.TRUE.toString().equalsIgnoreCase(this.yaml.string(Roles.ENABLED));
        }

        /**
         * Get supplier to read user permissions from storage.
         * @return User permissions supplier
         */
        Supplier<PermissionCollection> perms() {
            return () -> {
                final PermissionCollection res;
                if (this.enabled()) {
                    res = YamlPolicy.readPermissionsFromYaml(this.yaml);
                } else {
                    res = new EmptyPermissions();
                }
                return res;
            };
        }

        /**
         * Get user roles collection.
         * @return Roles collection
         */
        Collection<String> roles() {
            Set<String> roles = Collections.emptySet();
            if (this.enabled()) {
                final YamlSequence sequence = this.yaml.yamlSequence("roles");
                if (sequence != null) {
                    roles = sequence.values().stream().map(item -> item.asScalar().value())
                        .collect(Collectors.toSet());
                }
            }
            return roles;
        }
    }
}
