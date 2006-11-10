package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import org.jscience.mathematics.vectors.Vector;

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
        return getOwner().getVectorToEnemy();
    }
}
