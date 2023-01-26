/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.policy;

import com.artipie.asto.Key;
import com.artipie.asto.blocking.BlockingStorage;
import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.security.perms.EmptyPermissions;
import java.nio.charset.StandardCharsets;
import java.security.Permissions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test for {@link YamlPolicy.Roles}.
 * @since 1.2
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class YamlPolicyRolesTest {

    /**
     * Test storage.
     */
    private BlockingStorage asto;

    @BeforeEach
    void init() {
        this.asto = new BlockingStorage(new InMemoryStorage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"java-dev.yml", "java-dev.yaml"})
    void readsRolePermissions(final String key) {
        this.asto.save(new Key.From("roles", key), this.javaDev());
        MatcherAssert.assertThat(
            new YamlPolicy.Roles(this.asto).apply("java-dev"),
            new IsInstanceOf(Permissions.class)
        );
    }

    @Test
    void readsAdminPermissions() {
        this.asto.save(new Key.From("roles/admin.yaml"), this.admin());
        MatcherAssert.assertThat(
            new YamlPolicy.Roles(this.asto).apply("admin"),
            new IsInstanceOf(Permissions.class)
        );
    }

    @Test
    void returnsEmptyPermissionsIfRoleDisabled() {
        this.asto.save(new Key.From("roles/some-role.yaml"), this.disabled());
        MatcherAssert.assertThat(
            new YamlPolicy.Roles(this.asto).apply("some-role"),
            new IsInstanceOf(EmptyPermissions.class)
        );
    }

    @Test
    void returnsEmptyPermissionsIfFileDoesNotExists() {
        MatcherAssert.assertThat(
            new YamlPolicy.Roles(this.asto).apply("any"),
            new IsInstanceOf(EmptyPermissions.class)
        );
    }

    private byte[] javaDev() {
        return String.join(
            "\n",
            "java-dev:",
            "  permissions:",
            "    adapter_basic_permission:",
            "      maven-repo:",
            "        - read",
            "        - write",
            "      python-repo:",
            "        - read",
            "      npm-repo:",
            "        - read"
        ).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] admin() {
        return String.join(
            "\n",
            "admin:",
            "  permissions:",
            "    adapter_all_permission: {}"
        ).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] disabled() {
        return String.join(
            "\n",
            "some-role:",
            "  enabled: false",
            "  permissions:",
            "    adapter_basic_permission:",
            "      maven-repo:",
            "        - read",
            "        - write",
            "      python-repo:",
            "        - read",
            "      npm-repo:",
            "        - read"
        ).getBytes(StandardCharsets.UTF_8);
    }

}
