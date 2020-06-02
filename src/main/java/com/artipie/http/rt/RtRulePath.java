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

import com.artipie.http.Response;
import com.artipie.http.Slice;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import org.reactivestreams.Publisher;

/**
 * Rule-based route path.
 * <p>
 * A path to slice with routing rule. If
 * {@link RtRule} passed, then the request will be redirected to
 * underlying {@link Slice}.
 * </p>
 * @since 0.10
 */
public final class RtRulePath implements RtPath {

    /**
     * Routing rule.
     */
    private final RtRule rule;

    /**
     * Slice under route.
     */
    private final Slice slice;

    /**
     * New routing path.
     * @param rule Rules to apply
     * @param slice Slice to call
     */
    public RtRulePath(final RtRule rule, final Slice slice) {
        this.rule = rule;
        this.slice = slice;
    }

    @Override
    public Optional<Response> response(
        final String line,
        final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body
    ) {
        final Optional<Response> res;
        if (this.rule.apply(line, headers)) {
            res = Optional.of(this.slice.response(line, headers, body));
        } else {
            res = Optional.empty();
        }
        return res;
    }
}
