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

import com.artipie.http.Response;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.rs.RsStatus;
import com.artipie.http.rs.RsWithStatus;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AsyncResponse}.
 *
 * @since 0.8
 */
class AsyncResponseTest {

    @Test
    void shouldSend() {
        final CompletableFuture<RsWithStatus> future = CompletableFuture.completedFuture(
            new RsWithStatus(RsStatus.OK)
        );
        MatcherAssert.assertThat(
            new AsyncResponse(future),
            new RsHasStatus(RsStatus.OK)
        );
    }

    @Test
    void shouldPropagateFailure() {
        final CompletableFuture<Response> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalStateException());
        final CompletionStage<Void> result = new AsyncResponse(future)
            .send((status, headers, body) -> CompletableFuture.allOf());
        MatcherAssert.assertThat(
            result.toCompletableFuture().isCompletedExceptionally(),
            new IsEqual<>(true)
        );
    }
}
