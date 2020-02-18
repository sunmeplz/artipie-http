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
package com.artipie.http.rs;

/**
 * HTTP response status code.
 * See <a href="https://tools.ietf.org/html/rfc2616#section-6.1.1">RFC 2616 6.1.1 Status Code and Reason Phrase</a>
 *
 * @since 0.4
 */
public final class RsStatus {

    /**
     * OK.
     */
    public static final RsStatus OK = new RsStatus("200");

    /**
     * Created.
     */
    public static final RsStatus CREATED = new RsStatus("201");

    /**
     * Accepted.
     */
    public static final RsStatus ACCEPTED = new RsStatus("202");

    /**
     * Moved Permanently.
     */
    public static final RsStatus MOVED_PERMANENTLY = new RsStatus("301");

    /**
     * Found.
     */
    public static final RsStatus FOUND = new RsStatus("302");

    /**
     * Not Modified.
     */
    public static final RsStatus NOT_MODIFIED = new RsStatus("304");

    /**
     * Temporary Redirect.
     */
    @SuppressWarnings("PMD.LongVariable")
    public static final RsStatus TEMPORARY_REDIRECT = new RsStatus("307");

    /**
     * Bad Request.
     */
    public static final RsStatus BAD_REQUEST = new RsStatus("400");

    /**
     * Unauthorized.
     */
    public static final RsStatus UNAUTHORIZED = new RsStatus("401");

    /**
     * Forbidden.
     */
    public static final RsStatus FORBIDDEN = new RsStatus("403");

    /**
     * Not Found.
     */
    public static final RsStatus NOT_FOUND = new RsStatus("404");

    /**
     * Method Not Allowed.
     */
    @SuppressWarnings("PMD.LongVariable")
    public static final RsStatus METHOD_NOT_ALLOWED = new RsStatus("405");

    /**
     * Request Time-out.
     */
    public static final RsStatus REQUEST_TIMEOUT = new RsStatus("408");

    /**
     * Conflict.
     */
    public static final RsStatus CONFLICT = new RsStatus("409");

    /**
     * Internal Server Error.
     */
    public static final RsStatus INTERNAL_ERROR = new RsStatus("500");

    /**
     * Not Implemented.
     */
    public static final RsStatus NOT_IMPLEMENTED = new RsStatus("501");

    /**
     * Service Unavailable.
     */
    public static final RsStatus UNAVAILABLE = new RsStatus("503");

    /**
     * Code value.
     */
    private final String string;

    /**
     * Ctor.
     *
     * @param string Code value.
     */
    private RsStatus(final String string) {
        this.string = string;
    }

    /**
     * Code as 3-digit string.
     *
     * @return Code as 3-digit string.
     */
    public String code() {
        return this.string;
    }
}
