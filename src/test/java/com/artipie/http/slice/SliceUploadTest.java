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

import com.artipie.asto.Key;
import com.artipie.asto.Remaining;
import com.artipie.asto.Storage;
import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.http.hm.RsHasStatus;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rs.RsStatus;
import io.reactivex.Flowable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SliceUpload}.
 * @since 0.6
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class SliceUploadTest {

    @Test
    void uploadsKeyByPath() throws Exception {
        final Storage storage = new InMemoryStorage();
        final String hello = "Hello";
        final byte[] data = hello.getBytes(StandardCharsets.UTF_8);
        final String path = "uploads/file.txt";
        MatcherAssert.assertThat(
            "Wrong HTTP status returned",
            new SliceUpload(storage).response(
                new RequestLine("PUT", path, "HTTP/1.1").toString(),
                Collections.singleton(
                    new MapEntry<>("Content-Size", Long.toString(data.length))
                ),
                Flowable.just(ByteBuffer.wrap(data))
            ),
            new RsHasStatus(RsStatus.CREATED)
        );
        MatcherAssert.assertThat(
            new String(
                new Remaining(
                    Flowable.fromPublisher(storage.value(new Key.From(path)).get()).toList()
                        .blockingGet().get(0)
                ).bytes(),
                StandardCharsets.UTF_8
            ),
            new IsEqual<>(hello)
        );
    }
}
