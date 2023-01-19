/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.policy;

import com.amihaiemil.eoyaml.YamlMapping;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Policy configuration.
 * @since 1.2
 */
public interface PolicyConfig {

    /**
     * Gets string value.
     *
     * @param key Key.
     * @return Value.
     */
    String string(String key);

    /**
     * Gets sequence of values.
     *
     * @param key Key.
     * @return Sequence.
     */
    Collection<String> sequence(String key);

    /**
     * Gets subconfig.
     *
     * @param key Key.
     * @return Config.
     */
    PolicyConfig config(String key);

    /**
     * Policy configuration from yaml.
     * @since 1.2
     */
    class Yaml implements PolicyConfig {

        /**
         * Yaml mapping source to read config from.
         */
        private final YamlMapping source;

        /**
         * Ctor.
         * @param yaml Yaml mapping source to read config from
         */
        public Yaml(final YamlMapping yaml) {
            this.source = yaml;
        }

        @Override
        public String string(final String key) {
            return this.source.string(key);
        }

        @Override
        public Collection<String> sequence(final String key) {
            return Optional.ofNullable(this.source.yamlSequence(key))
                .map(
                    seq -> seq.values().stream()
                        .map(item -> item.asScalar().value()).collect(Collectors.toList())
                ).orElse(Collections.emptyList());
        }

        @Override
        public PolicyConfig config(final String key) {
            return new Yaml(this.source.yamlMapping(key));
        }

        @Override
        public String toString() {
            return this.source.toString();
        }

    }
}
