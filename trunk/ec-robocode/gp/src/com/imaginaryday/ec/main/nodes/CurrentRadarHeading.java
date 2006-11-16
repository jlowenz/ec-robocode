package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import static com.imaginaryday.util.Stuff.clampZero;
import static com.imaginaryday.util.Stuff.isReasonable;

public class CurrentRadarHeading extends RoboNode {

    @Override
    protected Node[] children() {
        return NONE;
    }

    public Class getInputType(int id) {
        return null;
    }

    public String getName() {
        return "currentRadarHeading";
    }

    public Class getOutputType() {
        return Number.class;
    }

    @Override
    public Object evaluate() {
        double heading = clampZero(getOwner().getRadarHeadingRadians());
        assert !(Double.isNaN(heading) || Double.isInfinite(heading)) : "heading was bad!";
        assert isReasonable(heading) : "unreasonable value: " + heading;

        return heading;
    }
}
