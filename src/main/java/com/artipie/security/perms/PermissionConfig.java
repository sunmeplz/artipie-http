/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.perms;

import com.amihaiemil.eoyaml.YamlMapping;
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
            return this.yaml.keys().iterator().next().asScalar().value();
        }

        @Override
        public String value(final String key) {
            return this.yaml.yamlMapping(this.name()).string(key);
        }

        @Override
        public Set<String> sequence(final String key) {
            return this.yaml.yamlSequence(key).values().stream().map(
                item -> item.asScalar().value()
            ).collect(Collectors.toSet());
        }
    }
}
