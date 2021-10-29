/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.slice;

import com.artipie.asto.Key;
import com.artipie.asto.Storage;
import com.artipie.asto.blocking.BlockingStorage;
import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.http.headers.ContentDisposition;
import com.artipie.http.headers.ContentLength;
import com.artipie.http.hm.RsHasHeaders;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.hm.SliceHasResponse;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rq.RqMethod;
import com.artipie.http.rs.RsFull;
import com.artipie.http.rs.RsStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BlobMetadataSlice}.
 *
 * @since 0.26.2
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class BlobMetadataSliceTest {

    /**
     * Storage.
     */
    private final Storage storage = new InMemoryStorage();

    /**
     * Tests that headers are correct when returning found.
     *  @todo #397:30min Fix {@link RsFull} that duplicates header.
     *   We notice (in this test) that due to the empty content, {@link RsFull}
     *   firstly add a content length with value of 0. After that, BlobMetadataSlice
     *   adds a content length with size of the blob found. We think that {@link RsFull}
     *   should avoid header duplication. It should overwrite the existing one. After
     *   fixing this issue, remove {@code new ContentLength(0)} in the test below.
     */
    @Test
    void returnsFound() {
        final Key key = new Key.From("foo");
        final Key another = new Key.From("bar");
        new BlockingStorage(this.storage).save(key, "anything".getBytes());
        new BlockingStorage(this.storage).save(another, "another".getBytes());
        MatcherAssert.assertThat(
            new BlobMetadataSlice(this.storage),
            new SliceHasResponse(
                Matchers.allOf(
                    new RsHasStatus(RsStatus.OK),
                    new RsHasHeaders(
                        new ContentLength(8),
                        new ContentDisposition("attachment; filename=\"foo\""),
                        new ContentLength(0)
                    )
                ),
                new RequestLine(RqMethod.HEAD, "/foo")
            )
        );
    }

    @Test
    void returnsNotFound() {
        MatcherAssert.assertThat(
            new SliceDelete(this.storage),
            new SliceHasResponse(
                new RsHasStatus(RsStatus.NOT_FOUND),
                new RequestLine(RqMethod.DELETE, "/bar")
            )
        );
    }
}

