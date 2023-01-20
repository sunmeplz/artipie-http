/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.policy;

import com.artipie.asto.Key;
import com.artipie.asto.blocking.BlockingStorage;
import com.artipie.asto.memory.InMemoryStorage;
import java.nio.charset.StandardCharsets;
import java.security.Permissions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test for {@link YamlPolicy.User}.
 * @since 1.2
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class YamlPolicyUserTest {

    /**
     * Test storage.
     */
    private BlockingStorage asto;

    @BeforeEach
    void init() {
        this.asto = new BlockingStorage(new InMemoryStorage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"users/alice.yml", "users/alice.yaml"})
    void readsUserRoles(final String key) {
        this.asto.save(new Key.From(key), this.aliceConfig());
        MatcherAssert.assertThat(
            new YamlPolicy.User(this.asto, "alice").roles(),
            Matchers.containsInAnyOrder("java-dev", "unknown-role")
        );
    }

    @Test
    void returnsEmptySetWhenUserDoesNotHaveRoles() {
        this.asto.save(new Key.From("users/david.yaml"), this.davidConfig());
        MatcherAssert.assertThat(
            new YamlPolicy.User(this.asto, "david").roles(),
            Matchers.empty()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"users/alice.yml", "users/alice.yaml"})
    void readUserPerms(final String key) {
        this.asto.save(new Key.From(key), this.aliceConfig());
        MatcherAssert.assertThat(
            new YamlPolicy.User(this.asto, "alice").perms().get(),
            new IsInstanceOf(Permissions.class)
        );
    }

    @Test
    void readsAllPerm() {
        this.asto.save(new Key.From("users/david.yml"), this.davidConfig());
        MatcherAssert.assertThat(
            new YamlPolicy.User(this.asto, "david").perms().get(),
            new IsInstanceOf(Permissions.class)
        );
    }

    private byte[] aliceConfig() {
        return String.join(
            "\n",
            "alice:",
            "  type: plain",
            "  pass: qwerty",
            "  email: alice@example.com",
            "  enabled: true",
            "  roles:",
            "    - java-dev",
            "    - unknown-role",
            "  permissions:",
            "    adapter_basic_permission:",
            "      rpm-repo:",
            "        - read",
            "      binary-test-repo:",
            "        - read",
            "        - write"
        ).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] davidConfig() {
        return String.join(
            "\n",
            "david:",
            "  type: plain",
            "  pass: qwerty",
            "  email: david@example.com",
            "  permissions:",
            "    adapter_all_permission: {}",
            "  enabled: true"
            ).getBytes(StandardCharsets.UTF_8);
    }

}
