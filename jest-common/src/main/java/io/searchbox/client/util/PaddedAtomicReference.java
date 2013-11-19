/*
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 */

package io.searchbox.client.util;

import java.util.concurrent.atomic.AtomicReference;

public class PaddedAtomicReference<T> extends AtomicReference<T> {
    public long p2, p3, p4, p5, p6, p7;

    public PaddedAtomicReference() {
        super();
    }

    public PaddedAtomicReference(T t) {
        super(t);
    }

    public long sumPadded() {
        return p2 + p3 + p4 + p5 + p6 + p7;
    }
}