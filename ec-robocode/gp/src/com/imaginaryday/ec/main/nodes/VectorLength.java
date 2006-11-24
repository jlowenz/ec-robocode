package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;
import com.imaginaryday.util.VectorUtils;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 5:46:21 PM<br>
 * </b>
 */
public class VectorLength extends RoboNode {
    private Node child[] = new Node[1];

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "vectorLen";
    }
    public Class getInputType(int id) {
        return Vector.class;
    }
    public Class getOutputType() {
        return Number.class;
    }
    public Object evaluate() {
        VectorFloat64 vec = (VectorFloat64)child[0].evaluate();
        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vec.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vec.y was bad!";
        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;

        double length = VectorUtils.vecLength(vec);
        assert !(Double.isNaN(length) || Double.isInfinite(length)) : "length was bad! vec: " + vec;
        assert Stuff.isReasonable(length) : "unreasonable value: " + length;
        return length;
    }
}
