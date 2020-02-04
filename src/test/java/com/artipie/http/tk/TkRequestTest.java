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

package com.artipie.http.tk;

import io.reactivex.rxjava3.internal.operators.flowable.FlowableFromPublisher;
import java.util.List;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;

/**
 * Test case for {@link TkRequest}.
 * @since 0.1
 */
final class TkRequestTest {
    @Test
    void readsRequestLine() throws Exception {
        MatcherAssert.assertThat(
            new TkRequest(new RqFake("PUT")).line(),
            Matchers.equalTo("PUT /")
        );
    }

    @Test
    void readsHeasers() throws Exception {
        MatcherAssert.assertThat(
            new TkRequest(
                new RqWithHeader(
                    new RqWithHeader(
                        new RqFake(new ListOf<>("GET /", "Host: localhost"), ""),
                        "X-Test", "1"
                    ),
                    "X-TesT", "2"
                )
            ).headers(),
            Matchers.allOf(
                Matchers.aMapWithSize(2),
                Matchers.hasEntry(
                    Matchers.containsStringIgnoringCase("host"),
                    Matchers.containsInAnyOrder("localhost")
                ),
                Matchers.hasEntry(
                    Matchers.containsStringIgnoringCase("x-test"),
                    Matchers.containsInAnyOrder("1", "2")
                )
            )
        );
    }

    @Test
    void readsEmptyHeaders() throws Exception {
        MatcherAssert.assertThat(
            new TkRequest(new RqFake(new ListOf<>("POST /"), "")).headers(),
            Matchers.anEmptyMap()
        );
    }

    @Test
    void readsBody() throws Exception {
        final byte[] body = new byte[]{0x00, 0x01, 0x02, 0x03};
        MatcherAssert.assertThat(
            new FlowableFromPublisher<>(
                FlowAdapters.toPublisher(
                    new TkRequest(new RqFake(new ListOf<>("PUT /foo"), body)).body()
                )
            ).toList().map(TkRequestTest::toPrimitives).blockingGet(),
            Matchers.equalTo(body)
        );
    }

    /**
     * Convert list of bytes to primitives.
     * @param src Source list
     * @return Primitive bytes
     */
    private static byte[] toPrimitives(final List<Byte> src) {
        final byte[] res = new byte[src.size()];
        for (int pos = 0; pos < res.length; ++pos) {
            res[pos] = src.get(pos).byteValue();
        }
        return res;
    }
}
