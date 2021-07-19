package com.artipie.http.rq.multipart;

import com.artipie.http.rq.RqHeaders;
import io.reactivex.Flowable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.subjects.SingleSubject;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Test case for {@link MultiPart}.
 *
 * @since 1.0
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle ReturnCountCheck (500 lines)
 */
@SuppressWarnings(
        {
                "PMD.TestClassWithoutTestCases", "PMD.OnlyOneReturn",
                "PMD.JUnit4TestShouldUseBeforeAnnotation"
        }
)
public final class MultiPartsTckTest
        extends PublisherVerification<Integer> {

    private final TestEnvironment env;

    /**
     * Ctor.
     */
    public MultiPartsTckTest() {
        this(new TestEnvironment(true));
    }

    public MultiPartsTckTest(TestEnvironment env) {
        super(env);
        this.env = env;
    }

    @Override
    public Publisher<Integer> createPublisher(final long size) {
        final String boundary = "--bnd";
        final MultiParts target = new MultiParts(boundary);
        target.subscribeAsync(Flowable.rangeLong(0, size).map(id -> newChunk(id, id == size - 1, boundary)));
        return Flowable.fromPublisher(target).flatMapSingle(
                part -> Flowable.fromPublisher(part).reduce(0, (acc, buf) -> acc + buf.remaining())
        );
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
       return null;
    }

    @Override
    public long maxElementsFromPublisher() {
        return 20;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }

    private ByteBuffer newChunk(final long id, final boolean end, final String boundary) {
        final StringBuilder sb = new StringBuilder();
        if (id == 0) {
            sb.append("\r\n");
        }
        sb.append(boundary)
                .append("\r\n")
                .append("Id: ").append(id).append("\r\n")
                .append("\r\n")
                .append("<id>").append(id).append("</id>")
                .append("\r\n");
        if (end) {
            sb.append(String.format("%s--", boundary)).append("\r\n");
        }
        this.env.debug(String.format("newChunk(id=%d, env=%b, boundary='%s') -> %s", id, end, boundary, sb.toString()));
        return ByteBuffer.wrap(sb.toString().getBytes(StandardCharsets.US_ASCII));
    }
}