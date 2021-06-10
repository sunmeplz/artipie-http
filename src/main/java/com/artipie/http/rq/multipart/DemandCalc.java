/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/npm-adapter/LICENSE.txt
 */
package com.artipie.http.rq.multipart;

import java.util.function.LongUnaryOperator;

/**
 * New reactive demand calculator.
 * <p>
 * It implements {@link LongUnaryOperator} by adding two long numbers
 * and it count boundary and special cases defining in reactive-streams
 * specification, such as sum of {@link Long.MAX_VALUE} with any number is
 * {@link Long.MAX_VALUE}.
 * <br/>
 * It's fast enough to perform CAS operations in atomic values.
 * </p>
 * @since 1.0
 */
public final class DemandCalc implements LongUnaryOperator {

    /**
     * Demand calculator for MAX long value.
     */
    private static final LongUnaryOperator MAX = (old) -> Long.MAX_VALUE;

    /**
     * Demand calculator for zero value.
     */
    private static final LongUnaryOperator ZERO = (old) -> old;

    /**
     * Amount to add.
     */
    private final long add;

    /**
     * New demand calculator.
     * @param add Amount ot add
     */
    private DemandCalc(long add) {
        this.add = add;
    }

    @Override
    public long applyAsLong(long old) {
        final long next;
        final long tmp = old + this.add;
        if (tmp < 0) {
            next = Long.MAX_VALUE;
        } else {
            next = tmp;
        }
        return next;
    }

    /**
     * New demand calculator for adding amount to old value.
     * @param amount To add
     * @return Calculator operator
     */
    public static LongUnaryOperator add(final long amount) {
        final LongUnaryOperator res;
        if (amount < 0) {
            throw new IllegalStateException("amount can't be less than zero");
        }
        if (amount == Long.MAX_VALUE) {
            res = DemandCalc.MAX;
        } else if (amount == 0) {
            res = DemandCalc.ZERO;
        } else {
            res = new DemandCalc(amount);
        }
        return res;
    }
}
