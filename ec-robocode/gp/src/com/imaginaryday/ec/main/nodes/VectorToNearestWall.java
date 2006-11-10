package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import org.jscience.mathematics.vectors.Vector;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:03:57 PM<br>
 * </b>
 */
public class VectorToNearestWall extends RoboNode {
    public String getName() {
        return "vectorToNearWall";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Vector.class;
    }

    protected Node[] children() {
        return NONE;
    }


    public Object evaluate() {
        return getOwner().getVectorToNearWall();
    }
}
