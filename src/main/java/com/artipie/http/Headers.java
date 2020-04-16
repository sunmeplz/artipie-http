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
package com.artipie.http;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import org.cactoos.map.MapEntry;

/**
 * HTTP request headers.
 *
 * @since 0.8
 * @checkstyle InterfaceIsTypeCheck (2 lines)
 */
public interface Headers extends Iterable<Map.Entry<String, String>> {

    /**
     * Empty headers.
     */
    Headers EMPTY = new From(Collections.emptyList());

    /**
     * {@link Headers} created from something.
     *
     * @since 0.8
     */
    final class From implements Headers {

        /**
         * Origin headers.
         */
        private final Iterable<Map.Entry<String, String>> origin;

        /**
         * Ctor.
         *
         * @param name Header name.
         * @param value Header value.
         */
        public From(final String name, final String value) {
            this(Collections.singleton(new MapEntry<>(name, value)));
        }

        /**
         * Ctor.
         *
         * @param origin Origin headers.
         */
        public From(final Iterable<Map.Entry<String, String>> origin) {
            this.origin = origin;
        }

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return this.origin.iterator();
        }

        @Override
        public void forEach(final Consumer<? super Map.Entry<String, String>> action) {
            this.origin.forEach(action);
        }

        @Override
        public Spliterator<Map.Entry<String, String>> spliterator() {
            return this.origin.spliterator();
        }
    }
}
