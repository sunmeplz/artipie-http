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
package com.artipie.http.async;

import com.artipie.http.Slice;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.rs.RsStatus;
import com.artipie.http.rs.RsWithStatus;
import com.artipie.http.slice.SliceSimple;
import io.reactivex.Flowable;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AsyncSlice}.
 *
 * @since 0.8
 */
class AsyncSliceTest {

    @Test
    void shouldRespond() {
        MatcherAssert.assertThat(
            new AsyncSlice(
                CompletableFuture.completedFuture(
                    new SliceSimple(new RsWithStatus(RsStatus.OK))
                )
            ).response("", Collections.emptySet(), Flowable.empty()),
            new RsHasStatus(RsStatus.OK)
        );
    }

    @Test
    void shouldPropagateFailure() {
        final CompletableFuture<Slice> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalStateException());
        MatcherAssert.assertThat(
            new AsyncSlice(future)
                .response("GET /index.html HTTP_1_1", Collections.emptySet(), Flowable.empty())
                .send((status, headers, body) -> CompletableFuture.allOf())
                .toCompletableFuture()
                .isCompletedExceptionally(),
            new IsEqual<>(true)
        );
    }
}
