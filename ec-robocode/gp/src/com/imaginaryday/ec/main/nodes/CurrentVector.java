package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 31, 2006<br>
 * Time: 9:53:51 PM<br>
 * </b>
 */
public class CurrentVector extends RoboNode {

    protected Node[] children() {
        return NONE;
    }

    public String getName() {
        return "currentVector";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Vector.class;
    }

    public Object evaluate() {
//        VectorFloat64 vec = getOwner().getCurrentVector();
//        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "x was bad!";
//        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "y was bad!";
//        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
//        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;
//        return vec;
	    return VectorFloat64.valueOf(0,-1);
    }
}
