package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 5:43:55 PM<br>
 * </b>
 */
public class ScaleVector extends RoboNode {
    private Node child[] = new Node[2];

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "scaleVector";
    }
    public Class getInputType(int id) {
        switch (id) {
            case 0: return Vector.class;
            case 1: return Number.class;
        }
        return null;
    }
    public Class getOutputType() {
        return Vector.class;
    }

    public Object evaluate() {
        double val = Stuff.clampZero(((Number)child[1].evaluate()).doubleValue());
        assert !(Double.isNaN(val) || Double.isInfinite(val)) : "scalar value was bad!";
        assert Stuff.isReasonable(val) : "unreasonable value: " + val;

        VectorFloat64 vec = (VectorFloat64)child[0].evaluate();
        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vec.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vec.y was bad!";
        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;

        if (Stuff.nearZero(val)) return vec; // sanitize - can't scale a vector to zero!

        VectorFloat64 newVec = vec.times(Float64.valueOf(val));
        assert !(Double.isNaN(newVec.getValue(0)) || Double.isInfinite(newVec.getValue(0))) : "newVec.x was bad!";
        assert !(Double.isNaN(newVec.getValue(1)) || Double.isInfinite(newVec.getValue(1))) : "newVec.y was bad!";
        assert Stuff.isReasonable(newVec.getValue(0)) : "unreasonable value: " + newVec;
        assert Stuff.isReasonable(newVec.getValue(1)) : "unreasonable value: " + newVec;

        return newVec;
    }
}
