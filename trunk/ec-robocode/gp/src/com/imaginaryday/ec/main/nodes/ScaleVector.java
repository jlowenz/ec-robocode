package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import org.jscience.mathematics.vectors.VectorFloat64;
import org.jscience.mathematics.numbers.Float64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 5:43:55 PM<br>
 * </b>
 */
public class ScaleVector extends RoboNode {
    private RoboNode child[] = new RoboNode[2];

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "scaleVector";
    }
    public Class getInputType(int id) {
        switch (id) {
            case 0: return VectorFloat64.class;
            case 1: return Number.class;
        }
        return null;
    }
    public Class getOutputType() {
        return VectorFloat64.class;
    }

    public Object evaluate() {
        double val = (Double)child[1].evaluate();
        VectorFloat64 vec = (VectorFloat64)child[0].evaluate();
        return vec.times(Float64.valueOf(val));
    }
}
