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
package com.artipie.http.headers;

import com.artipie.http.Headers;
import com.artipie.http.rq.RqHeaders;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Sticky;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;

/**
 * Content-Disposition header.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition"></a>
 * @since 0.17.8
 */
public final class ContentDisposition extends Header.Wrap {

    /**
     * Header name.
     */
    public static final String NAME = "Content-Disposition";

    /**
     * Header directives.
     */
    private final Map<String, String> directives;

    /**
     * Ctor.
     *
     * @param value Header value.
     * @todo #212:30m Update cactoos.
     *  Bump cactoos to the latest version and remove Sticky
     *  decorator from defintion of `directives`.
     */
    public ContentDisposition(final String value) {
        super(new Header(ContentDisposition.NAME, value));
        this.directives = new MapOf<>(
            new Sticky<>(
                new IterableOf<>(
                    new Iterator<Map.Entry<String, String>>() {
                        private final Matcher matcher = Pattern.compile(
                            "(?<key> \\w+ ) (?:= [\"] (?<value> [^\"]+ ) [\"] )?[;]?",
                            Pattern.COMMENTS
                        ).matcher(value);

                        @Override
                        public boolean hasNext() {
                            return this.matcher.find();
                        }

                        @Override
                        public Map.Entry<String, String> next() {
                            return new MapEntry<>(
                                this.matcher.group("key"),
                                this.matcher.group("value")
                            );
                        }
                    }
                )
            )
        );
    }

    /**
     * Ctor.
     *
     * @param headers Headers to extract header from.
     */
    public ContentDisposition(final Headers headers) {
        this(new RqHeaders.Single(headers, ContentDisposition.NAME).asString());
    }

    /**
     * The original name of the file transmitted.
     *
     * @return String.
     */
    public String fileName() {
        return this.directives.get("filename");
    }

    /**
     * The name of the HTML field in the form
     * that the content of this subpart refers to.
     *
     * @return String.
     */
    public String fieldName() {
        return this.directives.get("name");
    }

    /**
     * Inline.
     *
     * @return Boolean flag.
     */
    public Boolean isInline() {
        return this.directives.containsKey("inline");
    }

    /**
     * Inline.
     *
     * @return Boolean flag.
     */
    public Boolean isAttachment() {
        return this.directives.containsKey("attachment");
    }

}
