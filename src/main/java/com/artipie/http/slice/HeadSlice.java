/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.slice;

import com.artipie.asto.Key;
import com.artipie.asto.Storage;
import com.artipie.http.Headers;
import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.async.AsyncResponse;
import com.artipie.http.headers.ContentFileName;
import com.artipie.http.headers.ContentLength;
import com.artipie.http.rq.RequestLineFrom;
import com.artipie.http.rs.RsFull;
import com.artipie.http.rs.RsWithBody;
import com.artipie.http.rs.RsWithHeaders;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import com.artipie.http.rs.StandardRs;
import org.reactivestreams.Publisher;

/**
 * A {@link Slice} which only serves metadata on Binary files.
 *
 * @since 0.26.2
 * @todo #397:30min Use this class in artipie/files-adapter.
 *  We should replace {@link HeadSlice} of artipie/files-adapter by
 *  this one. Before doing this task, we need to fix {@link RsFull} about header
 *  duplication and the next release of artipie/http.
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class HeadSlice implements Slice {

    /**
     * Storage.
     */
    private final Storage storage;

    /**
     * Path to key transformation.
     */
    private final Function<String, Key> transform;

    /**
     * Slice by key from storage.
     *
     * @param storage Storage
     */
    public HeadSlice(final Storage storage) {
        this(storage, KeyFromPath::new);
    }

    /**
     * Slice by key from storage using custom URI path transformation.
     *
     * @param storage Storage
     * @param transform Transformation
     */
    public HeadSlice(
        final Storage storage,
        final Function<String, Key> transform) {
        this.storage = storage;
        this.transform = transform;
    }

    @Override
    public Response response(
        final String line,
        final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body
    ) {
        return new AsyncResponse(
            CompletableFuture
                .supplyAsync(new RequestLineFrom(line)::uri)
                .thenCompose(
                    uri -> {
                        final Key key = this.transform.apply(uri.getPath());
                        return this.storage.exists(key)
                            .thenCompose(
                                exist -> {
                                    final CompletionStage<Response> result;
                                    if (exist) {
                                        result = this.storage.size(key)
                                            .thenApply(
                                                size ->
                                                    new RsWithHeaders(
                                                        StandardRs.OK,
                                                        new Headers.From(
                                                            new ContentFileName(uri),
                                                            new ContentLength(size)
                                                        )
                                                    )
                                            );
                                    } else {
                                        result = CompletableFuture.completedFuture(
                                            new RsWithBody(
                                                StandardRs.NOT_FOUND,
                                                String.format("Key %s not found", key.string()),
                                                StandardCharsets.UTF_8
                                            )
                                        );
                                    }
                                    return result;
                                }
                            );
                    }
                )
        );
    }

}
