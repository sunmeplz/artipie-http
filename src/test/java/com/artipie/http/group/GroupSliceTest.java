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
package com.artipie.http.group;

import com.artipie.http.Slice;
import com.artipie.http.async.AsyncSlice;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.hm.SliceHasResponse;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rq.RqMethod;
import com.artipie.http.rs.RsStatus;
import com.artipie.http.rs.StandardRs;
import com.artipie.http.slice.SliceSimple;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link GroupSlice}.
 *
 * @since 1.0
 */
final class GroupSliceTest {

    @Test
    void groupFromMultipleSources() {
        // @checkstyle MagicNumberCheck (10 lines)
        MatcherAssert.assertThat(
            new GroupSlice(
                new SliceWithDelay(new SliceSimple(StandardRs.NOT_FOUND), Duration.ofMillis(250)),
                new SliceWithDelay(new SliceSimple(StandardRs.NOT_FOUND), Duration.ofMillis(50)),
                new SliceWithDelay(new SliceSimple(StandardRs.OK), Duration.ofMillis(150)),
                new SliceWithDelay(new SliceSimple(StandardRs.NOT_FOUND), Duration.ofMillis(200))
            ),
            new SliceHasResponse(
                new RsHasStatus(RsStatus.OK),
                new RequestLine(RqMethod.GET, "/")
            )
        );
    }

    /**
     * Slice testing decorator to add delay before sending request to origin slice.
     * @since 0.10
     */
    private static final class SliceWithDelay extends Slice.Wrap {

        /**
         * Add delay for slice.
         * @param origin Origin slice
         * @param delay Delay duration
         */
        SliceWithDelay(final Slice origin, final Duration delay) {
            super(
                new AsyncSlice(
                    CompletableFuture.runAsync(
                        () -> {
                            try {
                                Thread.sleep(delay.toMillis());
                            } catch (final InterruptedException ignore) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    ).thenApply(none -> origin)
                )
            );
        }
    }
}
