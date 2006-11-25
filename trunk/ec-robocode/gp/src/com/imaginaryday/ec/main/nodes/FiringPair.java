package com.imaginaryday.ec.main.nodes;

import info.javelot.functionalj.tuple.Pair;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 24, 2006<br>
 * Time: 5:41:23 PM<br>
 * </b>
 */
public class FiringPair extends Pair<Boolean,Number> {
    private static final long serialVersionUID = -4950907474152324079L;

    public FiringPair() {
    }
    public FiringPair(Boolean aBoolean, Number number) {
        super(aBoolean, number);
    }
}
