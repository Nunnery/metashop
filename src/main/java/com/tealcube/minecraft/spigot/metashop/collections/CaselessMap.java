/*
 * This file is part of Strife, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package com.tealcube.minecraft.spigot.metashop.collections;

import java.util.HashMap;
import java.util.Map;

public final class CaselessMap<T> extends HashMap<String, T> {

    public CaselessMap(Map<? extends String, ? extends T> m) {
        this();
        for (Map.Entry<? extends String, ? extends T> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public CaselessMap() {
        super();
    }

    @Override
    public T get(Object key) {
        String s = key.toString().toLowerCase();
        return super.get(s);
    }

    @Override
    public boolean containsKey(Object key) {
        String s = key.toString().toLowerCase();
        return super.containsKey(s);
    }

    @Override
    public T put(String key, T value) {
        String s = key.toLowerCase();
        return super.put(s, value);
    }

    @Override
    public T remove(Object key) {
        String s = key.toString().toLowerCase();
        return super.remove(s);
    }

}
