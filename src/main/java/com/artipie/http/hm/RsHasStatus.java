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

import com.artipie.http.Connection;
import com.artipie.http.Response;
import com.artipie.http.rs.RsStatus;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;
import org.reactivestreams.Publisher;

/**
 * Matcher to verify response status.
 * @since 0.1
 */
public final class RsHasStatus extends TypeSafeMatcher<Response> {

    /**
     * Status code matcher.
     */
    private final Matcher<RsStatus> status;

    /**
     * Ctor.
     * @param status Code to match
     */
    public RsHasStatus(final RsStatus status) {
        this(new IsEqual<>(status));
    }

    /**
     * Ctor.
     * @param status Code matcher
     */
    public RsHasStatus(final Matcher<RsStatus> status) {
        this.status = status;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendDescriptionOf(this.status);
    }

    @Override
    public boolean matchesSafely(final Response item) {
        final AtomicReference<RsStatus> out = new AtomicReference<>();
        item.send(new FakeConnection(out)).toCompletableFuture().join();
        return this.status.matches(out.get());
    }

    /**
     * Fake connection.
     * @since 0.1
     */
    private static final class FakeConnection implements Connection {

        /**
         * Status code container.
         */
        private final AtomicReference<RsStatus> container;

        /**
         * Ctor.
         * @param container Status code container
         */
        FakeConnection(final AtomicReference<RsStatus> container) {
            this.container = container;
        }

        @Override
        public CompletableFuture<Void> accept(
            final RsStatus status,
            final Iterable<Entry<String, String>> headers,
            final Publisher<ByteBuffer> body) {
            return CompletableFuture.supplyAsync(
                () -> {
                    this.container.set(status);
                    return null;
                }
            );
        }
    }
}
