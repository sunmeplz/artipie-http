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
package com.artipie.http.hm;

import java.nio.charset.Charset;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;

/**
 * Matcher to verify byte array as string.
 *
 * @since 0.7.2
 */
public final class IsString extends TypeSafeMatcher<byte[]> {

    /**
     * Charset used to decode bytes to string.
     */
    private final Charset charset;

    /**
     * String matcher.
     */
    private final Matcher<String> matcher;

    /**
     * Ctor.
     *
     * @param string String the bytes should be equal to.
     */
    public IsString(final String string) {
        this(Charset.defaultCharset(), new IsEqual<>(string));
    }

    /**
     * Ctor.
     *
     * @param charset Charset used to decode bytes to string.
     * @param string String the bytes should be equal to.
     */
    public IsString(final Charset charset, final String string) {
        this(charset, new IsEqual<>(string));
    }

    /**
     * Ctor.
     *
     * @param matcher Matcher for string.
     */
    public IsString(final Matcher<String> matcher) {
        this(Charset.defaultCharset(), matcher);
    }

    /**
     * Ctor.
     *
     * @param charset Charset used to decode bytes to string.
     * @param matcher Matcher for string.
     */
    public IsString(final Charset charset, final Matcher<String> matcher) {
        this.charset = charset;
        this.matcher = matcher;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("bytes ").appendDescriptionOf(this.matcher);
    }

    @Override
    public boolean matchesSafely(final byte[] item) {
        final String string = new String(item, this.charset);
        return this.matcher.matches(string);
    }
}
