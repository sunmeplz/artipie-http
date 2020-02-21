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

import com.artipie.http.Slice;
import com.artipie.vertx.VertxSliceServer;
import io.reactivex.Flowable;
import io.vertx.reactivex.core.Vertx;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;

import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.multipart.MultipartForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.reactivestreams.FlowAdapters;

/**
 * Multipart parser test.
 *
 * @since 0.4
 */
public class MpTest {

    private static final String LOCALHOST = "localhost";

    @Test
    public void ableToParseBasic(@TempDir final Path dir) throws IOException {
        final Vertx vertx = Vertx.vertx();
        final Slice slice = (line, headers, body) -> {
            return connection -> {
                for (Map.Entry<String, String> header : headers) {
                    System.out.println(header);
                }
                final int zero = 0;
                final int okay = 200;
                final Flowable<ByteBuffer> flowable = Flowable.fromPublisher(FlowAdapters.toPublisher(body));
                flowable.subscribe(byteBuffer -> {
                    final byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    final String s = new String(bytes);
                    System.out.println(s);
                });
                final Mp mp = new Mp(headers);
//                body.subscribe(mp);
//                final Flowable<Part> partFlowable = Flowable.fromPublisher(FlowAdapters.toPublisher(mp));
//                partFlowable.subscribe();
                connection.accept(
                    okay,
                    new HashSet<>(zero),
                    FlowAdapters.toFlowPublisher(Flowable.empty())
                );
            };
        };
        final int port = this.rndPort();
        final VertxSliceServer server = new VertxSliceServer(vertx, slice, port);
        server.start();
        final Path resolve = dir.resolve("text.txt");
        Files.write(resolve, "Hello worrrrld!!!".getBytes());
        final WebClient web = WebClient.create(vertx);
        web.post(port, MpTest.LOCALHOST, "/hello")
            .rxSendMultipartForm(
                MultipartForm.create()
                    .textFileUpload(
                        "hello",
                        resolve.getFileName().toString(),
                        resolve.toAbsolutePath().toString(),
                        "text/plain"
                    )
                    .textFileUpload(
                        "hello2",
                        resolve.getFileName().toString(),
                        resolve.toAbsolutePath().toString(),
                        "text/plain"
                    )
            ).blockingGet();
        server.stop();
        vertx.close();
    }

    /**
     * Find a random port.
     *
     * @return The free port.
     * @throws IOException If fails.
     */
    private int rndPort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
