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
package com.artipie.http.rt;

import com.artipie.http.rq.RequestLineFrom;
import com.artipie.http.rq.RqMethod;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Route by HTTP methods rule.
 * @since 0.16
 */
public final class RlByMethods implements RtRule {

    /**
     * Standard method rules.
     * @since 0.16
     */
    public enum Standard implements RtRule {
        /**
         * Rule for {@code GET} method.
         */
        GET(new RlByMethods(RqMethod.GET)),
        /**
         * Rule for {@code POST} method.
         */
        POST(new RlByMethods(RqMethod.POST)),
        /**
         * Rule for {@code PUT} method.
         */
        PUT(new RlByMethods(RqMethod.PUT)),
        /**
         * Rule for {@code DELETE} method.
         */
        DELETE(new RlByMethods(RqMethod.DELETE)),
        /**
         * All common read methods.
         */
        ALL_READ(new RlByMethods(RqMethod.GET, RqMethod.HEAD, RqMethod.OPTIONS)),
        /**
         * All common write methods.
         */
        ALL_WRITE(new RlByMethods(RqMethod.PUT, RqMethod.POST, RqMethod.DELETE, RqMethod.PATCH));

        /**
         * Origin rule.
         */
        private final RtRule origin;

        /**
         * Ctor.
         * @param origin Rule
         */
        Standard(final RtRule origin) {
            this.origin = origin;
        }

        @Override
        public boolean apply(final String line,
            final Iterable<Map.Entry<String, String>> headers) {
            return this.origin.apply(line, headers);
        }
    }

    /**
     * Method name.
     */
    private final Set<RqMethod> methods;

    /**
     * Route by methods.
     * @param methods Method names
     */
    public RlByMethods(final RqMethod... methods) {
        this(new HashSet<>(Arrays.asList(methods)));
    }

    /**
     * Route by methods.
     * @param methods Method names
     */
    public RlByMethods(final Set<RqMethod> methods) {
        this.methods = Collections.unmodifiableSet(methods);
    }

    @Override
    public boolean apply(final String line,
        final Iterable<Map.Entry<String, String>> headers) {
        return this.methods.contains(new RequestLineFrom(line).method());
    }
}
