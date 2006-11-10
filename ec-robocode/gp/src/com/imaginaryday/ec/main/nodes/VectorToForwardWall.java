package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import org.jscience.mathematics.vectors.Vector;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 9:34:43 PM<br>
 * </b>
 */
public class VectorToForwardWall extends RoboNode {
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
        return getOwner().getVectorToForwardWall();
    }
}
