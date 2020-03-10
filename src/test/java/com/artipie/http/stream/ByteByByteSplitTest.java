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
package com.artipie.http.stream;

import io.reactivex.Flowable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.RepeatedTest;

/**
 * Tests for {@link ByteByByteSplit}.
 *
 * @since 0.4
 * @checkstyle MagicNumberCheck (500 lines)
 */
public final class ByteByByteSplitTest {

    @RepeatedTest(100)
    public void basicSplitWorks() {
        final ByteByByteSplit split = new ByteByByteSplit(" ".getBytes());
        Flowable.fromArray(
            Arrays.stream(
                ArrayUtils.toObject("how are you".getBytes())
            ).map(
                (Byte aByte) -> {
                    final byte[] bytes = new byte[1];
                    bytes[0] = aByte;
                    return ByteBuffer.wrap(bytes);
                }
            ).toArray(ByteBuffer[]::new)
        ).subscribe(split);
        MatcherAssert.assertThat(
            "howareyou",
            new IsEqual<>(
                new String(
                    Flowable.fromPublisher(split)
                        .flatMap(pub -> pub)
                        .toList()
                        .blockingGet()
                        .stream()
                        .map(
                            byteBuffer -> {
                                final byte[] res = new byte[byteBuffer.remaining()];
                                byteBuffer.get(res);
                                return res;
                            }
                        )
                        .reduce(
                            (one, another) -> {
                                final byte[] res = new byte[one.length + another.length];
                                System.arraycopy(one, 0, res, 0, one.length);
                                System.arraycopy(another, 0, res, one.length, another.length);
                                return res;
                            }
                        )
                        .get()
                )
            )
        );
    }

}
