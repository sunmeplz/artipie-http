package com.artipie.http.stream;

import io.reactivex.Flowable;
import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SplitTest {

    @Test
    public void basicSplit() {
        Flowable<ByteBuffer> flow = Flowable.fromArray(
            Arrays.stream(
                ArrayUtils.toObject("how are you".getBytes())
            ).map((Byte aByte) -> {
                final byte[] bytes = new byte[1];
                bytes[0] = aByte;
                return ByteBuffer.wrap(bytes);
            }).toArray(ByteBuffer[]::new)
        );
        final ByteByByteSplit split = new ByteByByteSplit(" ".getBytes(), ring);
        flow.subscribe(split);
        final String actual = new String(
            Flowable.fromPublisher(split)
                .flatMap(byteBufferPublisher -> byteBufferPublisher)
                .toList()
                .blockingGet()
                .stream()
                .map(byteBuffer -> {
                    final byte[] res = new byte[byteBuffer.remaining()];
                    byteBuffer.get(res);
                    return res;
                })
                .reduce((a, b) -> {
                    byte[] c = new byte[a.length + b.length];
                    System.arraycopy(a, 0, c, 0, a.length);
                    System.arraycopy(b, 0, c, a.length, b.length);
                    return c;
                })
                .get()
        );
        MatcherAssert.assertThat(
            "howareyou",
            new IsEqual<>(actual)
        );
    }

}
