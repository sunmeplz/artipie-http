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
package com.artipie.http.hm;

import com.artipie.http.Headers;
import com.artipie.http.rq.RqHeaders;
import java.util.Collections;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher verifies that request headers filtered by name are matched against target matcher.
 * @since 0.10
 */
public class RqHasHeader extends TypeSafeMatcher<Headers> {

    /**
     * Headers name.
     */
    private final String name;

    /**
     * Headers matcher.
     */
    private final Matcher<? extends Iterable<? extends String>> matcher;

    /**
     * Match headers by name against target matcher.
     * @param name Headers name
     * @param matcher Target matcher
     */
    public RqHasHeader(final String name,
        final Matcher<? extends Iterable<? extends String>> matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    @Override
    public final boolean matchesSafely(final Headers item) {
        return this.matcher.matches(new RqHeaders(item, this.name));
    }

    @Override
    public final void describeTo(final Description description) {
        description.appendText(String.format("Headers '%s': ", this.name))
            .appendDescriptionOf(this.matcher);
    }

    /**
     * Matcher for single header.
     * @since 0.10
     */
    public static final class Single extends RqHasHeader {

        /**
         * Matche header by name against value matcher.
         * @param name Header name
         * @param header Value matcher
         */
        public Single(final String name, final Matcher<String> header) {
            super(name, Matchers.contains(Collections.singletonList(header)));
        }

        /**
         * Match header by name against its value.
         * @param name Header name
         * @param header Header value
         */
        public Single(final String name, final String header) {
            this(name, Matchers.equalTo(header));
        }
    }
}
