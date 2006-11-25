package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Nov 6, 2006
 * Time: 5:42:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class IsTurretMoving extends RoboNode {
    private static final long serialVersionUID = -6100391100365489576L;

    protected Node[] children() {
        return NONE;
    }

    public String getName() {
        return "isTurretMoving";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Boolean.class;
    }

    public Object evaluate() {
        return getOwner().getGunTurnRemaining() != 0.0;
    }
}
