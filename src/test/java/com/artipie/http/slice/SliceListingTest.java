/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.slice;

import com.artipie.asto.Content;
import com.artipie.asto.Key;
import com.artipie.asto.Storage;
import com.artipie.asto.memory.InMemoryStorage;
import com.artipie.http.hm.ResponseMatcher;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rs.RsStatus;
import io.reactivex.Flowable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import javax.json.Json;
import org.cactoos.map.MapEntry;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link SliceListingTest}.
 * @since 1.1.4
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class SliceListingTest {

    @ParameterizedTest
    @CsvSource({
        "not-exists/, ''",
        "one/, 'one/target1.txt\none/two/target2.txt'"
    })
    void responseTextType(final String path, final String body) {
        final Storage storage = new InMemoryStorage();
        storage.save(new Key.From("one/target1.txt"), new Content.Empty()).join();
        storage.save(new Key.From("one/two/target2.txt"), new Content.Empty()).join();
        MatcherAssert.assertThat(
            new SliceListing(storage, "text/plain", ListingFormat.Standard.TEXT).response(
                get(path), Collections.emptyList(), Flowable.empty()
            ),
            new ResponseMatcher(
                RsStatus.OK,
                Arrays.asList(
                    new MapEntry<>(
                        "Content-Type",
                        String.format("text/plain; charset=%s", StandardCharsets.UTF_8)
                    ),
                    new MapEntry<>("Content-Length", String.valueOf(body.length()))
                ),
                body.getBytes(StandardCharsets.UTF_8)
            )
        );
    }

    @Test
    void responseJsonType() {
        final String one = "one/example1.txt";
        final String two = "one/two/example2.txt";
        final Storage storage = new InMemoryStorage();
        storage.save(new Key.From(one), new Content.Empty()).join();
        storage.save(new Key.From(two), new Content.Empty()).join();
        final String json = Json.createArrayBuilder(Arrays.asList(one, two)).build().toString();
        MatcherAssert.assertThat(
            new SliceListing(storage, "application/json", ListingFormat.Standard.JSON)
                .response(get("one/"), Collections.emptyList(), Flowable.empty()),
            new ResponseMatcher(
                RsStatus.OK,
                Arrays.asList(
                    new MapEntry<>(
                        "Content-Type",
                        String.format("application/json; charset=%s", StandardCharsets.UTF_8)
                    ),
                    new MapEntry<>("Content-Length", String.valueOf(json.length()))
                ),
                json.getBytes(StandardCharsets.UTF_8)
            )
        );
    }

    private static String get(final String path) {
        return new RequestLine("GET", path, "HTTP/1.1").toString();
    }
}
