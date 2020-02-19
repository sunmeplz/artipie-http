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

import com.artipie.http.Connection;
import com.artipie.http.Response;
import com.artipie.http.rs.RsWithBody;
import com.artipie.http.rs.RsWithStatus;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionStage;

/**
 * Asynchronous {@link Response} implementation.
 * <p>
 * Response which send itself to connection only when
 * underlying {@link CompletionStage} with response is ready.
 * If completion stage fails, this decorator sends 500 status code with
 * message text as a body.
 * </p>
 * @since 0.4
 */
public final class RsAsync implements Response {

    /**
     * Async response.
     */
    private final CompletionStage<Response> rsp;

    /**
     * Ctor.
     * @param rsp Response
     */
    RsAsync(final CompletionStage<Response> rsp) {
        this.rsp = rsp;
    }

    @Override
    public void send(final Connection con) {
        this.rsp.exceptionally(
            err -> new RsWithStatus(
                new RsWithBody(err.getMessage(), StandardCharsets.UTF_8),
                // @checkstyle MagicNumberCheck (1 line)
                500
            )
        ).thenAccept(target -> target.send(con));
    }
}
