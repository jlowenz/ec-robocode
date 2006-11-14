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
	    double dr = getOwner().getDistanceRemaining();
	    double tr = getOwner().getTurnRemainingRadians();
	    if (Math.abs(dr) < 0.0001) return false;
	    return Math.abs(tr) >= 0.0001;
    }
}
