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

/**
 * HTTP request method.
 * See <a href="https://tools.ietf.org/html/rfc2616#section-5.1.1">RFC 2616 5.1.1 Method</a>
 *
 * @since 0.4
 */
public final class RqMethod {

    /**
     * OPTIONS.
     */
    public static final RqMethod OPTIONS = new RqMethod("OPTIONS");

    /**
     * GET.
     */
    public static final RqMethod GET = new RqMethod("GET");

    /**
     * HEAD.
     */
    public static final RqMethod HEAD = new RqMethod("HEAD");

    /**
     * POST.
     */
    public static final RqMethod POST = new RqMethod("POST");

    /**
     * PUT.
     */
    public static final RqMethod PUT = new RqMethod("PUT");

    /**
     * DELETE.
     */
    public static final RqMethod DELETE = new RqMethod("DELETE");

    /**
     * TRACE.
     */
    public static final RqMethod TRACE = new RqMethod("TRACE");

    /**
     * CONNECT.
     */
    public static final RqMethod CONNECT = new RqMethod("CONNECT");

    /**
     * String value.
     */
    private final String string;

    /**
     * Ctor.
     *
     * @param string String value.
     */
    private RqMethod(final String string) {
        this.string = string;
    }

    /**
     * Method string.
     *
     * @return Method string.
     */
    public String value() {
        return this.string;
    }
}
