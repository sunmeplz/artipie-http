/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import com.amihaiemil.eoyaml.Yaml;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link YamlPolicyFactory}.
 * @since 1.2
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class YamlPolicyFactoryTest {

    @Test
    void createsYamlPolicy() {
        MatcherAssert.assertThat(
            new YamlPolicyFactory().getPolicy(
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

}
