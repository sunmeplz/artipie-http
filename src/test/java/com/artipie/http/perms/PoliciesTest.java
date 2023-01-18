/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import com.amihaiemil.eoyaml.Yaml;
import com.artipie.ArtipieException;
import java.security.Permissions;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Policies}.
 * @since 1.2
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class PoliciesTest {

    @Test
    void createsYamlPolicy() {
        MatcherAssert.assertThat(
            new Policies().newPolicy(
                "yaml_policy",
                new PolicyConfig.Yaml(
                    Yaml.createYamlMappingBuilder().add("type", "yaml_policy")
                        .add(
                            "storage",
                            Yaml.createYamlMappingBuilder().add("type", "fs")
                                .add("path", "/some/path").build()
                        ).build()
                )
            ),
            new IsInstanceOf(YamlPolicy.class)
        );
    }

    @Test
    void throwsExceptionIfPermNotFound() {
        Assertions.assertThrows(
            ArtipieException.class,
            () -> new Policies().newPolicy(
                "unknown_policy",
                new PolicyConfig.Yaml(Yaml.createYamlMappingBuilder().build())
            )
        );
    }

    @Test
    void throwsExceptionIfPermissionsHaveTheSameName() {
        Assertions.assertThrows(
            ArtipieException.class,
            () -> new Policies(
                Collections.singletonMap(
                    Policies.SCAN_PACK, "custom.policy.db;custom.policy.duplicate"
                )
            )
        );
    }

    @Test
    void createsExternalPermissions() {
        final Policies policy = new Policies(
            Collections.singletonMap(
                Policies.SCAN_PACK, "custom.policy.db;custom.policy.file"
            )
        );
        MatcherAssert.assertThat(
            "Db policy was created",
            policy.newPolicy(
                "db-policy",
                new PolicyConfig.Yaml(Yaml.createYamlMappingBuilder().build())
            ),
            new IsInstanceOf(TestPolicy.class)
        );
        MatcherAssert.assertThat(
            "File policy was created",
            policy.newPolicy(
                "file-policy",
                new PolicyConfig.Yaml(Yaml.createYamlMappingBuilder().build())
            ),
            new IsInstanceOf(TestPolicy.class)
        );
    }

    /**
     * Test policy.
     * @since 1.2
     */
    public static final class TestPolicy implements Policy<Permissions> {

        @Override
        public Permissions getPermissions(final String uname) {
            return new Permissions();
        }
    }
}
