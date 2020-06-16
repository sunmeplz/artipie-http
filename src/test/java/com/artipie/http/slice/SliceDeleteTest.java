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
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rs.RsStatus;
import io.reactivex.Flowable;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SliceDelete}.
 *
 * @since 0.10
 */
public final class SliceDeleteTest {

    /**
     * Deleted key.
     */
    private static final String KEY = "deletedKey";

    /**
     * Delete method.
     */
    private static final String METHOD = "DELETE";

    /**
     * HTTP.
     */
    private static final String HTTP = "HTTP/1.1";

    @Test
    @Disabled
    void deleteCorrectEntry() {
        final Storage storage = new InMemoryStorage();
        storage.save(
            new Key.From(SliceDeleteTest.KEY),
            new Content.From("deleted content".getBytes())
        ).join();
        new SliceDelete(
            storage
        ).response(
            new RequestLine(
                SliceDeleteTest.METHOD,
                SliceDeleteTest.KEY,
                SliceDeleteTest.HTTP
            ).toString(),
            Collections.emptyList(),
            Flowable.empty()
        );
        MatcherAssert.assertThat(
            storage.exists(new Key.From(SliceDeleteTest.KEY)),
            new IsEqual<>(false)
        );
    }

    @Test
    @Disabled
    void doNotDeleteOtherEntry() {
        final Storage storage = new InMemoryStorage();
        final Key preserved = new Key.From("preservedKey");
        storage.save(
            new Key.From(SliceDeleteTest.KEY),
            new Content.From("any content".getBytes())
        ).join();
        storage.save(
            preserved,
            new Content.From("preserved content".getBytes())
        ).join();
        new SliceDelete(
            storage
        ).response(
            new RequestLine(
                SliceDeleteTest.METHOD,
                SliceDeleteTest.KEY,
                SliceDeleteTest.HTTP
            ).toString(),
            Collections.emptyList(),
            Flowable.empty()
        );
        MatcherAssert.assertThat(
            storage.exists(preserved),
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
                new RequestLine(
                    SliceDeleteTest.METHOD,
                    "notfound",
                    SliceDeleteTest.HTTP
                ).toString(),
                Collections.emptyList(),
                Flowable.empty()
            ),
            new RsHasStatus(
                RsStatus.NOT_FOUND
            )
        );
    }
}

