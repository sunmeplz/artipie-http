/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import com.amihaiemil.eoyaml.Yaml;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link PermissionConfig.Yaml}.
 * Yaml permissions format example:
 *
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
 * {@link PermissionConfig.Yaml} implementation will receive mapping for single permission
 * adapter_basic_permission instance, for example:
 *
 * maven-repo:
 *   - read
 *   - write
 *
 * @since 1.2
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class PermissionConfigYamlTest {

    @Test
    void readsName() throws IOException {
        MatcherAssert.assertThat(
            new PermissionConfig.Yaml(
                Yaml.createYamlInput(
                    String.join(
                        "\n",
                        "maven-repo:",
                        "  - read"
                    )
                ).readYamlMapping()
            ).name(),
            new IsEqual<>("maven-repo")
        );
    }

    @Test
    void readsSequence() throws IOException {
        MatcherAssert.assertThat(
            new PermissionConfig.Yaml(
                Yaml.createYamlInput(
                    String.join(
                        "\n",
                        "some-repo:",
                        "  - read",
                        "  - write",
                        "  - delete"
                    )
                ).readYamlMapping()
            ).sequence("some-repo"),
            Matchers.contains("read", "write", "delete")
        );
    }

    @Test
    void readValueByKey() throws IOException {
        MatcherAssert.assertThat(
            new PermissionConfig.Yaml(
                Yaml.createYamlInput(
                    String.join(
                        "\n",
                        "some-repo:",
                        "  key: value",
                        "  key1: value2"
                    )
                ).readYamlMapping()
            ).value("key"),
            new IsEqual<>("value")
        );
    }
}
