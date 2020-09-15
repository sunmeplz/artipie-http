/*
 * MIT License
 *
 * Copyright (c) 2020 Artipie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.artipie.http.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Artipie permission action.
 * @since 0.16
 */
public interface Action {

    /**
     * Action names collection.
     * @return Names of the action
     */
    Collection<String> names();

    /**
     * Action enumeration.
     * @since 0.16
     */
    enum Standard implements Action {

        /**
         * Read artifact.
         */
        READ {
            @Override
            public Collection<String> names() {
                return Arrays.asList("r", "read", "download", "install");
            }
        },

        /**
         * Write artifact.
         */
        WRITE {
            @Override
            public Collection<String> names() {
                return Arrays.asList("w", "write", "publish", "push", "deploy", "upload");
            }
        },

        /**
         * Delete artifact.
         */
        DELETE {
            @Override
            public Collection<String> names() {
                return Arrays.asList("d", "delete");
            }
        };

    }

    /**
     * Instance of {@link Standard} by string.
     * @since 0.16
     */
    final class ByString {

        /**
         * Action string.
         */
        private final String action;

        /**
         * Ctor.
         * @param action String action
         */
        public ByString(final String action) {
            this.action = action;
        }

        /**
         * Returns instance of {@link Standard} by string synonym.
         * @return Instance of {@link Standard}
         */
        public Action get() {
            return Stream.of(Standard.values()).filter(
                item -> item.names().stream().anyMatch(act -> act.equals(this.action))
            ).findFirst().orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Unrecognized action %s", this.action)
                )
            );
        }

    }

}
