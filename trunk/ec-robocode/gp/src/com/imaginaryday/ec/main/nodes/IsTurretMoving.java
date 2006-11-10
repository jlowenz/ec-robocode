package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Nov 6, 2006
 * Time: 5:42:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class IsTurretMoving extends RoboNode {

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
