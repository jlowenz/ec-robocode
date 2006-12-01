package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.util.Tuple;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 30, 2006<br>
 * Time: 9:39:24 PM<br>
 * </b>
 */
public class DirectionPair extends Tuple.Two<VectorFloat64,Boolean> {
    private static final long serialVersionUID = 9062716866387230652L;

    public DirectionPair() {
    }
    public DirectionPair(VectorFloat64 vectorFloat64, Boolean aBoolean) {
        super(vectorFloat64, aBoolean);
    }
}
