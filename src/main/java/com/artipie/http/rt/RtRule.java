package com.artipie.http.rt;

import com.artipie.http.rq.RequestLineFrom;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.cactoos.list.ListOf;

/**
 * Routing rule.
 * @since 0.5
 */
public interface RtRule {

    /**
     * Apply this rule to request.
     * @param line Request line
     * @param headers Request headers
     * @return True if rule passed
     */
    boolean apply(String line, Iterable<Map.Entry<String, String>> headers);

    /**
     * Route by multiple rules.
     * @since 0.5
     */
    final class Multiple implements RtRule {

        /**
         * Rules.
         */
        private final Iterable<RtRule> rules;

        /**
         * Route by multiple rules.
         * @param rules Rules array
         */
        public Multiple(final RtRule... rules) {
            this(new ListOf<>(rules));
        }

        /**
         * Route by multiple rules.
         * @param rules Rules
         */
        public Multiple(final Iterable<RtRule> rules) {
            this.rules = rules;
        }

        @Override
        public boolean apply(final String line,
            final Iterable<Map.Entry<String, String>> headers) {
            boolean match = true;
            for (final RtRule rule : this.rules) {
                if (!rule.apply(line, headers)) {
                    match = false;
                    break;
                }
            }
            return match;
        }
    }

    /**
     * Route by method.
     * @since 0.5
     */
    final class ByMethod implements RtRule {

        /**
         * Method name.
         */
        private final String method;

        /**
         * Route by method.
         * @param method
         */
        public ByMethod(final String method) {
            this.method = method;
        }

        @Override
        public boolean apply(final String line,
            final Iterable<Map.Entry<String, String>> headers) {
            return Objects.equals(new RequestLineFrom(line).method(), this.method);
        }
    }

    /**
     * Route by path.
     * @since 0.5
     */
    final class ByPath implements RtRule {

        /**
         * Request URI path pattern.
         */
        private final Pattern ptn;

        /**
         * By path rule.
         * @param ptn Path pattern string
         */
        public ByPath(final String ptn) {
            this(Pattern.compile(ptn));
        }

        /**
         * By path rule.
         * @param ptn Path pattern
         */
        public ByPath(final Pattern ptn) {
            this.ptn = ptn;
        }

        @Override
        public boolean apply(final String line,
            final Iterable<Map.Entry<String, String>> headers) {
            return this.ptn.matcher(
                new RequestLineFrom(line).uri().getPath()
            ).matches();
        }
    }
}
