package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 9:34:43 PM<br>
 * </b>
 */
public class VectorToForwardWall extends RoboNode {
    private static final long serialVersionUID = 7804563496768737940L;
    protected Node[] children() {
        return NONE;
    }

    public String getName() {
        return "vectorToFwdWall";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Vector.class;
    }

    public Object evaluate() {
        VectorFloat64 vec = getOwner().getVectorToForwardWall();
        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vecToFwdWall.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vecToFwdWall.y was bad!";
        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;
        return vec;
    }
}
