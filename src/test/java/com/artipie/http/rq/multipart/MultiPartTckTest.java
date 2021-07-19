package com.artipie.http.rq.multipart;

import io.reactivex.internal.functions.Functions;
import io.reactivex.subjects.SingleSubject;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Executors;

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
public final class MultiPartTckTest
        extends PublisherVerification<ByteBuffer> {

    /**
     * Ctor.
     */
    public MultiPartTckTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<ByteBuffer> createPublisher(final long size) {
        SingleSubject<RqMultipart.Part> subj = SingleSubject.create();
        final MultiPart part = new MultiPart(
                Completion.FAKE,
                subj::onSuccess
        );
        Executors.newCachedThreadPool().submit(
                () -> {
                    part.push(ByteBuffer.wrap("\r\n\r\n".getBytes()));
                    byte[] data = new byte[4096];
                    Arrays.fill(data, (byte) 'A');
                    System.out.printf("prepare data, size=%d\n", size);
                    for (long pos = size; pos > 0; pos--) {
                        System.out.printf("pushing %d/%d\n", size - pos, size);
                        part.push(ByteBuffer.wrap(data));
                    }
                    System.out.println("flushing");
                    part.flush();
                }
        );

        return subj.flatMapPublisher(Functions.identity());
    }

    @Override
    public Publisher<ByteBuffer> createFailedPublisher() {
       return null;
    }

    @Override
    public long maxElementsFromPublisher() {
        return 1;
    }

    @Override
    public long boundedDepthOfOnNextAndRequestRecursion() {
        return 1;
    }
}