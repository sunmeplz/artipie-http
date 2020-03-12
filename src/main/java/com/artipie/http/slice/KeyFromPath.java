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
package com.artipie.http.slice;

import com.artipie.asto.Key;

/**
 * Key from path.
 * @since 0.6
 */
public final class KeyFromPath extends Key.Wrap {

    /**
     * Key from path string.
     * @param path Path string
     */
    public KeyFromPath(final String path) {
        super(new Key.From(normalize(path)));
    }

    /**
     * Normalize path to use as a valid {@link Key}.
     * Removes leading slash char if exist.
     * @param path Path string
     * @return Normalized path
     */
    private static String normalize(final String path) {
        final String res;
        if (path.length() > 0 && path.charAt(0) == '/') {
            res = path.substring(1);
        } else {
            res = path;
        }
        return res;
    }
}
