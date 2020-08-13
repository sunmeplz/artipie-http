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
package com.artipie.http.slice;

import com.artipie.asto.Content;
import com.artipie.asto.Key;
import com.artipie.asto.Storage;
import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.http.Slice;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rs.RsStatus;
import io.reactivex.Flowable;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SliceDelete}.
 *
 * @since 0.10
 */
final class SliceDeleteTest {

    @Test
    @Disabled
    void deleteCorrectEntry() throws Exception {
        final Storage storage = new InMemoryStorage();
        final String key = "deletedKey";
        storage.save(
            new Key.From(key),
            new Content.From("deleted content".getBytes())
        ).join();
        verify(
            new SliceDelete(
                storage
            ),
            line(key)
        );
        MatcherAssert.assertThat(
            storage.exists(new Key.From(key)).toCompletableFuture().get(),
            new IsEqual<>(false)
        );
    }

    @Test
    @Disabled
    void doNotDeleteOtherEntry() throws Exception {
        final Storage storage = new InMemoryStorage();
        final Key preserved = new Key.From("preservedKey");
        final String deleted = "deleted";
        storage.save(
            new Key.From(deleted),
            new Content.From("any content".getBytes())
        ).join();
        storage.save(
            preserved,
            new Content.From("preserved content".getBytes())
        ).join();
        verify(
            new SliceDelete(
                storage
            ),
            line(deleted)
        );
        MatcherAssert.assertThat(
            storage.exists(preserved).toCompletableFuture().get(),
            new IsEqual<>(true)
        );
    }

    @Test
    @Disabled
    void returnsNotFound() {
        final Storage storage = new InMemoryStorage();
        MatcherAssert.assertThat(
            new SliceDelete(
                storage
            ).response(
                line("notfound").toString(),
                Collections.emptyList(),
                Flowable.empty()
            ),
            new RsHasStatus(
                RsStatus.NOT_FOUND
            )
        );
    }

    private static RequestLine line(final String path) {
        return new RequestLine(
            "DELETE",
            path,
            "HTTP/1.1"
        );
    }

    private static void verify(final Slice slice, final RequestLine line) throws Exception {
        slice.response(line.toString(), Collections.emptyList(), Flowable.empty())
            .send((status, headers, body) -> CompletableFuture.completedFuture(null))
            .toCompletableFuture()
            .get();
    }
}

