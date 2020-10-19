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
package com.artipie.http.rs;

import com.artipie.asto.Content;
import com.artipie.asto.ext.PublisherAs;
import io.reactivex.Flowable;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link CachedResponse}.
 *
 * @since 0.17
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
class CachedResponseTest {

    @Test
    void shouldReadBodyOnFirstSend() {
        final AtomicBoolean terminated = new AtomicBoolean();
        final Flowable<ByteBuffer> publisher = Flowable.<ByteBuffer>empty()
            .doOnTerminate(() -> terminated.set(true));
        new CachedResponse(new RsWithBody(publisher)).send(
            (status, headers, body) -> CompletableFuture.allOf()
        ).toCompletableFuture().join();
        MatcherAssert.assertThat(terminated.get(), new IsEqual<>(true));
    }

    @Test
    void shouldReplayBody() {
        final byte[] content = "content".getBytes();
        final CachedResponse cached = new CachedResponse(
            new RsWithBody(new Content.OneTime(new Content.From(content)))
        );
        cached.send(
            (status, headers, body) -> CompletableFuture.allOf()
        ).toCompletableFuture().join();
        final AtomicReference<byte[]> capture = new AtomicReference<>();
        cached.send(
            (status, headers, body) -> new PublisherAs(body).bytes().thenAccept(capture::set)
        ).toCompletableFuture().join();
        MatcherAssert.assertThat(capture.get(), new IsEqual<>(content));
    }
}
