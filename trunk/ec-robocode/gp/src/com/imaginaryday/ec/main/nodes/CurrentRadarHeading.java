package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;

public class CurrentRadarHeading extends RoboNode {

    @Override
    protected Node[] children() {
        return NONE;
    }

    public Class getInputType(int id) {
        return null;
    }

    public String getName() {
        return "currentRadarHeader";
    }

    public Class getOutputType() {
        return Number.class;
    }

    @Override
    public Object evaluate() {
        double heading = getOwner().getRadarHeadingRadians();
        assert !(Double.isNaN(heading) || Double.isInfinite(heading)) : "heading was bad!";
        assert Stuff.isReasonable(heading) : "unreasonable value: " + heading;

        return heading;
    }
}
