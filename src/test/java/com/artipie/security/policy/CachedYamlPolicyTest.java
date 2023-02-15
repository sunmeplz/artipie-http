/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.policy;

import com.artipie.asto.Key;
import com.artipie.asto.blocking.BlockingStorage;
import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.security.perms.Action;
import com.artipie.security.perms.AdapterBasicPermission;
import com.artipie.security.perms.User;
import com.artipie.security.perms.UserPermissions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.nio.charset.StandardCharsets;
import java.security.PermissionCollection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link CachedYamlPolicy} and {@link UserPermissions}.
 * @since 1.2
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class CachedYamlPolicyTest {

    /**
     * Cache for usernames and {@link UserPermissions}.
     */
    private Cache<String, UserPermissions> cache;

    /**
     * Cache for usernames and user roles and individual permissions.
     */
    private Cache<String, User> user;

    /**
     * Cache for role name and role permissions.
     */
    private Cache<String, PermissionCollection> roles;

    /**
     * Storage to read users and roles yaml files from.
     */
    private BlockingStorage asto;

    @BeforeEach
    void init() {
        this.asto = new BlockingStorage(new InMemoryStorage());
        this.cache = CacheBuilder.newBuilder().build();
        this.user = CacheBuilder.newBuilder().build();
        this.roles = CacheBuilder.newBuilder().build();
    }

    @Test
    void aliceCanReadFromMavenWithJavaDevRole() {
        this.asto.save(new Key.From("users/alice.yml"), this.aliceConfig());
        this.asto.save(new Key.From("roles/java-dev.yaml"), this.javaDev());
        final CachedYamlPolicy policy = new CachedYamlPolicy(
            this.cache, this.user, this.roles, this.asto
        );
        MatcherAssert.assertThat(
            "Alice can read from maven repo",
            policy.getPermissions("alice")
                .implies(new AdapterBasicPermission("maven-repo", Action.Standard.READ)),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "Cache with UserPermissions has 1 item",
            this.cache.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with user has 1 item",
            this.user.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with role permissions has 1 item",
            this.roles.size(),
            new IsEqual<>(1L)
        );
        this.user.invalidateAll();
        MatcherAssert.assertThat(
            "Alice can read from maven repo",
            policy.getPermissions("alice")
                .implies(new AdapterBasicPermission("maven-repo", Action.Standard.READ)),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "Cache with UserPermissions has 1 item",
            this.cache.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with user roles and individual permissions has 0 items",
            this.user.size(),
            new IsEqual<>(0L)
        );
        MatcherAssert.assertThat(
            "Cache with role permissions has 1 item",
            this.roles.size(),
            new IsEqual<>(1L)
        );
    }

    @Test
    void aliceCanReadFromRpmRepoWithIndividualPerm() {
        this.asto.save(new Key.From("users/alice.yml"), this.aliceConfig());
        final CachedYamlPolicy policy = new CachedYamlPolicy(
            this.cache, this.user, this.roles, this.asto
        );
        MatcherAssert.assertThat(
            "Alice can read from rpm repo",
            policy.getPermissions("alice")
                .implies(new AdapterBasicPermission("rpm-repo", Action.Standard.READ)),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "Cache with UserPermissions has 1 item",
            this.cache.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with user individual permissions and roles has 1 item",
            this.user.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with role permissions has 0 items",
            this.roles.size(),
            new IsEqual<>(0L)
        );
    }

    @Test
    void aliceCanReadWithDevRoleAndThenWithIndividualPerms() {
        this.asto.save(new Key.From("users/alice.yml"), this.aliceConfig());
        this.asto.save(new Key.From("roles/java-dev.yaml"), this.javaDev());
        final CachedYamlPolicy policy = new CachedYamlPolicy(
            this.cache, this.user, this.roles, this.asto
        );
        MatcherAssert.assertThat(
            "Alice can read from maven repo",
            policy.getPermissions("alice")
                .implies(new AdapterBasicPermission("maven-repo", Action.Standard.READ)),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "Cache with UserPermissions has 1 item",
            this.cache.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with user individual permissions and roles has 1 item",
            this.user.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with role permissions has 1 item",
            this.roles.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Alice can read from rpm repo",
            policy.getPermissions("alice")
                .implies(new AdapterBasicPermission("rpm-repo", Action.Standard.READ)),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "Cache with UserPermissions has 1 item",
            this.cache.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with user individual permissions and roles has 1 item",
            this.user.size(),
            new IsEqual<>(1L)
        );
        MatcherAssert.assertThat(
            "Cache with role permissions has 1 item",
            this.roles.size(),
            new IsEqual<>(1L)
        );
    }

    private byte[] aliceConfig() {
        return String.join(
            "\n",
            "alice:",
            "  type: plain",
            "  pass: qwerty",
            "  email: alice@example.com",
            "  roles:",
            "    - java-dev",
            "  permissions:",
            "    adapter_basic_permission:",
            "      rpm-repo:",
            "        - read",
            "      binary-test-repo:",
            "        - read",
            "        - write"
        ).getBytes(StandardCharsets.UTF_8);
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
}
