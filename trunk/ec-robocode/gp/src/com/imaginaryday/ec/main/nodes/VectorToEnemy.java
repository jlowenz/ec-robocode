package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 11:00:51 PM<br>
 * </b>
 */
public class VectorToEnemy extends RoboNode {
    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "vectorToEnemy";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return Vector.class;
    }

    public Object evaluate() {
        VectorFloat64 vec = getOwner().getVectorToEnemy();
        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vecToEnemy.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vecToEnemy.y was bad!";
        return vec;
    }
}
