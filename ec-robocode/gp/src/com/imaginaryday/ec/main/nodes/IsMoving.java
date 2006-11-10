package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 31, 2006<br>
 * Time: 9:49:00 PM<br>
 * </b>
 */
public class IsMoving extends RoboNode {

    protected Node[] children() {
        return NONE;
    }

    public String getName() {
        return "isMoving";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Boolean.class;
    }

    public Object evaluate() {
        // TODO: can the values be negative here?
        if (getOwner().getDistanceRemaining() < 0 ||
                getOwner().getTurnRemaining() < 0) throw new RuntimeException("Negative values!");

        return (getOwner().getDistanceRemaining() > 0.0) ||
                (getOwner().getTurnRemaining() > 0.0);
    }
}
