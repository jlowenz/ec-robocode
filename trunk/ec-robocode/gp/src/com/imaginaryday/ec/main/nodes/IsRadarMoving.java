package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

public class IsRadarMoving extends RoboNode {

    @Override
    protected Node[] children() {
        return NONE;
    }

    public Class getInputType(int id) {
        return null;
    }

    public String getName() {
        return "isRadarMoving";
    }

    public Class getOutputType() {
        return Boolean.class;
    }

    @Override
    public Object evaluate() {
        return getOwner().getRadarTurnRemaining() > 0;
    }
}
