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
package com.artipie.http.rq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Multiparts}.
 *
 * @since 1.0
 * @checkstyle MagicNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class MultipartsTest {

    @Test
    void findNextBoundary() {
        final Multiparts target =
            new Multiparts(ByteBuffer.wrap("--abcd".getBytes(StandardCharsets.UTF_8)));
        MatcherAssert.assertThat(
            target.next(
                ByteBuffer.wrap("0000--abcd1111".getBytes(StandardCharsets.UTF_8)),
                0
            ),
            Matchers.equalTo(4)
        );
    }

    @Test
    void findBoundaryAtTheBeginning() {
        final Multiparts target =
            new Multiparts(ByteBuffer.wrap("--hjk".getBytes(StandardCharsets.UTF_8)));
        MatcherAssert.assertThat(
            target.next(
                ByteBuffer.wrap("--hjk000".getBytes(StandardCharsets.UTF_8)),
                0
            ),
            Matchers.equalTo(0)
        );
    }

    @Test
    void findNextBoundaryWithOffset() {
        final Multiparts target =
            new Multiparts(ByteBuffer.wrap("--zxc".getBytes(StandardCharsets.UTF_8)));
        MatcherAssert.assertThat(
            target.next(
                ByteBuffer.wrap("0000--zxc1111--zxc222".getBytes(StandardCharsets.UTF_8)),
                10
            ),
            Matchers.equalTo(13)
        );
    }

    @Test
    void findEndBoundary() {
        final Multiparts target =
            new Multiparts(ByteBuffer.wrap("--rty".getBytes(StandardCharsets.UTF_8)));
        MatcherAssert.assertThat(
            target.next(
                ByteBuffer.wrap("--rty123--rty".getBytes(StandardCharsets.UTF_8)),
                5
            ),
            Matchers.equalTo(8)
        );
    }

    @Test
    void findNoBoundaries() {
        final Multiparts target =
            new Multiparts(ByteBuffer.wrap("--abc".getBytes(StandardCharsets.UTF_8)));
        MatcherAssert.assertThat(
            target.next(
                ByteBuffer.wrap("000000000".getBytes(StandardCharsets.UTF_8)),
                0
            ),
            Matchers.equalTo(-1)
        );
    }

    @Test
    void findPossibleEnding() {
        final Multiparts target =
            new Multiparts(ByteBuffer.wrap("--fgh".getBytes(StandardCharsets.UTF_8)));
        MatcherAssert.assertThat(
            target.possibleEnding(
                ByteBuffer.wrap("asdzxcv--fg".getBytes(StandardCharsets.UTF_8))
            ),
            Matchers.is(true)
        );
    }

    @Test
    void pushFullBoundary() throws IOException {
        final ByteBuffer boundary =
            ByteBuffer.wrap("--bnd8".getBytes(StandardCharsets.UTF_8));
        final Multiparts multiparts = new Multiparts(boundary);
        final ByteBuffer buf = ByteBuffer.allocate(128);
        buf.put("--bnd8abc--bnd8".getBytes(StandardCharsets.UTF_8));
        buf.flip();
        MatcherAssert.assertThat(
            "failed to push multipart chunk",
            multiparts.push(buf), Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "wrong parts count was found",
            multiparts.popAll(), Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            "chunk is not tail boundary",
            buf.compareTo(boundary), Matchers.is(0)
        );
    }

    @Test
    void pushMultipleBoundaries() throws IOException {
        final ByteBuffer boundary =
            ByteBuffer.wrap("--bnd9".getBytes(StandardCharsets.UTF_8));
        final Multiparts multiparts = new Multiparts(boundary);
        final ByteBuffer buf = ByteBuffer.allocate(128);
        buf.put("--bnd9abc--bnd9zxc--bnd9qwe--bnd9".getBytes(StandardCharsets.UTF_8));
        buf.flip();
        while (multiparts.push(buf)) {
            buf.compact().flip();
        }
        MatcherAssert.assertThat(
            "wrong parts count was found",
            multiparts.popAll(), Matchers.hasSize(3)
        );
        MatcherAssert.assertThat(
            "chunk is not tail boundary",
            buf.compareTo(boundary), Matchers.is(0)
        );
    }

    @Test
    void pushMultipleChunks() throws IOException {
        final ByteBuffer boundary =
            ByteBuffer.wrap("--bnd3".getBytes(StandardCharsets.UTF_8));
        final Multiparts multiparts = new Multiparts(boundary);
        final ByteBuffer buf = ByteBuffer.allocate(128);
        buf.put("--bnd3as".getBytes(StandardCharsets.UTF_8)).flip();
        while (multiparts.push(buf)) {
            buf.compact().flip();
        }
        buf.compact().put("d--bnd3zxc--bn".getBytes(StandardCharsets.UTF_8)).flip();
        while (multiparts.push(buf)) {
            buf.compact().flip();
        }
        buf.compact().put("d3qwe--bnd3".getBytes(StandardCharsets.UTF_8)).flip();
        while (multiparts.push(buf)) {
            buf.compact().flip();
        }
        MatcherAssert.assertThat(
            "wrong parts count was found",
            multiparts.popAll(), Matchers.hasSize(3)
        );
        MatcherAssert.assertThat(
            "chunk is not tail boundary",
            buf.compareTo(boundary), Matchers.is(0)
        );
    }
}
