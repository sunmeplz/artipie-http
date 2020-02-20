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
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Multipart processor.
 * @since 0.4
 * @checkstyle MethodBodyCommentsCheck (500 lines)
 */
final class Multiparts {

    /**
     * Processed parts queue.
     */
    private final Deque<Path> parts;

    /**
     * Boundary data.
     */
    private final ByteBuffer boundary;

    /**
     * New processor.
     * @param boundary Boundary data
     */
    Multiparts(final ByteBuffer boundary) {
        this.parts = new LinkedList<>();
        this.boundary = boundary.duplicate().asReadOnlyBuffer();
    }

    /**
     * Push new chunk.
     * <p>
     * It tries to read multipart request from this chunk and process it.
     * If it parses the chunk sucecssfully it returns {@code true}
     * and move the position of buffer to the end of chunk boundary.
     * Client code should use this method in loop until it returns false
     * and compact chunk buffer:
     * <pre>
     *     <code>
     *         while (multiparts.push(chunk)) {
     *             chunk.compact().flip();
     *         }
     *     </code>
     * </pre>
     * This method processes only one request in one call.
     * False return value indicates that it can't process more requests.
     * Processed requests can be accessed via {@link #popAll()} method.
     * </p>
     * @param buf Chunk to process
     * @return True if processed a chunk
     * @throws IOException In case of failure
     * @todo #32:30min Try to refactor and simplify this method.
     *  It's a working example of how to parse multipart by chunks.
     *  Most probably it may be rewritten better and more readable.
     *  Keep all tests from MultipartTest class green after refactoring.
     * @checkstyle CyclomaticComplexityCheck (100 lines)
     * @checkstyle NestedIfDepthCheck (100 lines)
     * @checkstyle ExecutableStatementCountCheck (100 lines)
     * @checkstyle ReturnCountCheck (100 lines)
     */
    @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.CyclomaticComplexity"})
    public boolean push(final ByteBuffer buf) throws IOException {
        if (!buf.hasRemaining() || buf.remaining() <= this.boundary.remaining()) {
            return false;
        }
        final int start = this.next(buf, 0);
        final boolean result;
        if (start > 0) {
            // we already have current part from previous pushed chunk.
            // continue writing this chunk until new start
            // and move buffer position to next boundary position
            try (FileChannel chan = this.currentPart()) {
                final ByteBuffer slice = buf.slice(0, start);
                while (slice.hasRemaining()) {
                    chan.write(slice);
                }
            }
            buf.position(start);
            result = true;
        } else if (start == 0) {
            // it's the beginning of new part
            final int end = this.next(buf, start + this.boundary.remaining());
            // let's try to find next boundary as the end of current part
            if (end > 0) {
                if (end < start + this.boundary.remaining()) {
                    throw new IllegalStateException(
                        "wrong boundary token lookup, fix next() method"
                    );
                }
                // if we found next boundary, then write the part to new
                // file and complete successfully
                try (FileChannel chan = this.newPart()) {
                    final ByteBuffer slice = buf.slice(
                        start + this.boundary.remaining(),
                        end - start - this.boundary.remaining()
                    );
                    while (slice.hasRemaining()) {
                        chan.write(slice);
                    }
                }
                buf.position(end);
                result = true;
            } else {
                // if we can't find the end of boundary,
                // then check if current buffer is ended with
                // boundary symbols (partially), then we need to skip this
                // chunk and wait for next chunk to be sure that the part is complete
                if (this.possibleEnding(buf)) {
                    result = false;
                } else {
                    // if not, we write this chunk into part file
                    // (new part file, since start == 0)
                    try (FileChannel chan = this.newPart()) {
                        while (buf.hasRemaining()) {
                            chan.write(buf);
                        }
                    }
                    result = true;
                }
            }
        } else {
            // if no boundaries were found, then try to find
            // ending boundary. If it's possible boundary ending, then
            // skip this chunk and wait for next, else write the chunk into
            // current boundary file
            if (this.possibleEnding(buf)) {
                result = false;
            } else {
                try (FileChannel chan = this.currentPart()) {
                    while (buf.hasRemaining()) {
                        chan.write(buf);
                    }
                }
                result = true;
            }
        }
        return result;
    }

    /**
     * Take all parts and clear the list.
     * @return List of paths to parts
     */
    public List<Path> popAll() {
        final List<Path> paths = new ArrayList<>(this.parts);
        this.parts.clear();
        return paths;
    }

    /**
     * Check if buffer may be ended with a boundary.
     * @param buf Buffer to check
     * @return True if buffer may be ended with a boundary
     */
    boolean possibleEnding(final ByteBuffer buf) {
        final ByteBuffer bnd;
        if (buf.remaining() >= this.boundary.remaining()) {
            bnd = this.boundary.slice();
        } else {
            bnd = this.boundary.slice(0, buf.remaining());
        }
        boolean ending = false;
        for (int pos = 0; pos < bnd.remaining(); ++pos) {
            final ByteBuffer bslice = buf.slice(
                buf.remaining() - bnd.remaining() + pos,
                bnd.remaining() - pos
            );
            final ByteBuffer cmp = bnd.slice(0, bnd.remaining() - pos);
            if (bslice.compareTo(cmp) == 0) {
                ending = true;
                break;
            }
        }
        return ending;
    }

    /**
     * Next boundary position.
     * @param buf Buffer to check
     * @param offset Start offset
     * @return Position of next boundary
     */
    int next(final ByteBuffer buf, final int offset) {
        int res = -1;
        for (int pos = offset; pos <= buf.remaining() - this.boundary.remaining(); ++pos) {
            if (buf.slice(pos, this.boundary.remaining()).compareTo(this.boundary) == 0) {
                res = pos;
                break;
            }
        }
        return res;
    }

    /**
     * Take current part file channel.
     * @return File channel to part file
     * @throws IOException On failure
     */
    private FileChannel currentPart() throws IOException {
        return FileChannel.open(this.parts.getLast(), StandardOpenOption.APPEND);
    }

    /**
     * Create new part file.
     * @return New part file channel
     * @throws IOException On failure
     */
    private FileChannel newPart() throws IOException {
        final Path tmp = Files.createTempFile("part_", ".http");
        this.parts.addLast(tmp);
        return FileChannel.open(tmp, StandardOpenOption.WRITE);
    }
}
