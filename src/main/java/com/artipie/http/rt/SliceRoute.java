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
import com.artipie.http.rs.RsStatus;
import com.artipie.http.rs.RsWithBody;
import com.artipie.http.rs.RsWithStatus;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.cactoos.list.ListOf;
import org.reactivestreams.Publisher;

/**
 * Routing slice.
 * <p>
 * {@link Slice} implementation which redirect requests to {@link Slice}
 * in {@link SliceRoute.Path} if {@link RtRule} matched.<br/>
 * Usage:
 * <pre><code>
 * new SliceRoute(
 *   new SliceRoute.Path(
 *     new RtRule.ByMethod("GET"), new DownloadSlice(storage)
 *   ),
 *   new SliceRoute.Path(
 *     new RtRule.ByMethod("PUT"), new UploadSlice(storage)
 *   )
 * );
 * </code></pre>
 * </p>
 * @since 0.5
 * @todo #51:30min Refactor SliceRoute to get rid of null.
 *  This class is using null to iterate over optional responses and return first
 *  matched one in `response` method. Let's refactor it to avoid using null.
 */
public final class SliceRoute implements Slice {

    /**
     * Routes.
     */
    private final List<Path> routes;

    /**
     * New slice route.
     * @param routes Routes
     */
    public SliceRoute(final Path... routes) {
        this(new ListOf<>(routes));
    }

    /**
     * New slice route.
     * @param routes Routes
     */
    public SliceRoute(final List<Path> routes) {
        this.routes = routes;
    }

    @Override
    public Response response(final String line,
        final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        Response response = null;
        for (final Path item : this.routes) {
            final Optional<Response> opt = item.response(line, headers, body);
            if (opt.isPresent()) {
                response = opt.get();
                break;
            }
        }
        if (response == null) {
            response = new RsWithBody(
                new RsWithStatus(RsStatus.NOT_FOUND),
                "not found", StandardCharsets.UTF_8
            );
        }
        return response;
    }

    /**
     * Route path.
     * <p>
     * A path to slice with routing rule. If
     * {@link RtRule} passed, then the request will be redirected to
     * underlying {@link Slice}.
     * </p>
     * @since 0.5
     */
    public static final class Path {

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
        public Path(final RtRule rule, final Slice slice) {
            this.rule = rule;
            this.slice = slice;
        }

        /**
         * Try respond.
         * @param line Request line
         * @param headers Headers
         * @param body Body
         * @return Response if passed routing rule
         */
        Optional<Response> response(final String line,
            final Iterable<Map.Entry<String, String>> headers,
            final Publisher<ByteBuffer> body) {
            final Optional<Response> res;
            if (this.rule.apply(line, headers)) {
                res = Optional.of(this.slice.response(line, headers, body));
            } else {
                res = Optional.empty();
            }
            return res;
        }
    }
}
