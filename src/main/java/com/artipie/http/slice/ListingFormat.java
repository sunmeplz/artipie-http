/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */

package com.artipie.http.slice;

import com.artipie.asto.Key;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.json.Json;

/**
 * Format of a key collection.
 *
 * @since 1.1.1
 */
@FunctionalInterface
public interface ListingFormat {

    /**
     * Apply the format to the collection of keys.
     *
     * @param keys List of keys
     * @return Text formatted
     */
    String apply(Collection<? extends Key> keys);

    /**
     * Standard format implementations.
     * @since 1.1.0
     * @checkstyle IndentationCheck (30 lines)
     */
    enum Standard implements ListingFormat {
        /**
         * Text format renders keys as a list of strings
         * separated by newline char {@code \n}.
         */
        TEXT(
            keys -> keys.stream().map(Key::string).collect(Collectors.joining("\n"))
        ),

        /**
         * Json format renders keys as JSON array with
         * keys items.
         */
        JSON(
            keys -> Json.createArrayBuilder(
                keys.stream().map(Key::string).collect(Collectors.toList())
            ).build().toString()
        );

        /**
         * Format.
         */
        private final ListingFormat format;

        /**
         * Enum instance.
         *
         * @param fmt Format
         */
        Standard(final ListingFormat fmt) {
            this.format = fmt;
        }

        @Override
        public String apply(final Collection<? extends Key> keys) {
            return this.format.apply(keys);
        }
    }

}
