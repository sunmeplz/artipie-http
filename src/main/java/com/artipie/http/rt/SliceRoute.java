package com.artipie.http.rt;

import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.rs.RsWithBody;
import com.artipie.http.rs.RsWithStatus;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Flow;
import org.cactoos.list.ListOf;

/**
 * Routing slice.
 * @since 0.5
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
        final Flow.Publisher<ByteBuffer> body) {
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
                new RsWithStatus(404),
                "not found", StandardCharsets.UTF_8
            );
        }
        return response;
    }

    /**
     * Route path.
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
         * New rouing path.
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
            final Flow.Publisher<ByteBuffer> body) {
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
