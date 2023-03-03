/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.perms;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Permission configuration.
 * @since 1.2
 */
public interface PermissionConfig {

    /**
     * Permission name. The name can vary from the permission model,
     * in {@link AdapterBasicPermission} the name is the name of the repository.
     * @return Permission name
     */
    String name();

    /**
     * Gets string value.
     *
     * @param key Key.
     * @return Value.
     */
    String value(String key);

    /**
     * Gets sequence of values.
     *
     * @param key Key.
     * @return Sequence.
     */
    Set<String> sequence(String key);

    /**
     * Yaml permission config.
     * Implementation note:
     * Yaml permission config allows {@link AdapterBasicPermission#WILDCARD} yaml sequence.In
     * yamls `*` sign can be quoted. Thus, we need to handle various quotes properly.
     * @since 1.2
     */
    final class Yaml implements PermissionConfig {

        /**
         * Yaml mapping to read permission from.
         */
        private final YamlMapping yaml;

        /**
         * Ctor.
         * @param yaml Yaml mapping to read permission from
         */
        public Yaml(final YamlMapping yaml) {
            this.yaml = yaml;
        }

        @Override
        public String name() {
            return Yaml.cleanName(this.yaml.keys().iterator().next().asScalar().value());
        }

        @Override
        public String value(final String key) {
            return this.yaml.yamlMapping(this.name()).string(key);
        }

        @Override
        public Set<String> sequence(final String key) {
            final Set<String> res;
            if (AdapterBasicPermission.WILDCARD.equals(key)) {
                res = this.yaml.yamlSequence(
                    this.yaml.keys().stream().map(YamlNode::asScalar).filter(
                        item -> item.value().contains(AdapterBasicPermission.WILDCARD)
                    ).findFirst().orElseThrow(
                        () -> new IllegalStateException(String.format("Sequence %s not found", key))
                    )
                ).values().stream().map(
                    item -> item.asScalar().value()
                ).collect(Collectors.toSet());
            } else {
                res = this.yaml.yamlSequence(key).values().stream().map(
                    item -> item.asScalar().value()
                ).collect(Collectors.toSet());
            }
            return res;
        }

        /**
         * Cleans wildcard value from various escape signs.
         * @param value Value to check and clean
         * @return Cleaned value
         */
        private static String cleanName(final String value) {
            String res = value;
            if (value.contains(AdapterBasicPermission.WILDCARD)) {
                res = value.replace("\"", "").replace("'", "").replace("\\", "");
            }
            return res;
        }
    }
}
