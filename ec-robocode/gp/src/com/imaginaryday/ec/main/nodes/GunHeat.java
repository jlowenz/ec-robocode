package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.util.Stuff;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 9:33:43 PM<br>
 * </b>
 */
public class GunHeat extends RoboNode {

    protected Node[] children() {
        return NONE;
    }

    public String getName() {
        return "gunHeat";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        double heat = getOwner().getGunHeat();
        assert !(Double.isNaN(heat) || Double.isInfinite(heat)) : "heat was bad!";
        assert Stuff.isReasonable(heat) : "unreasonable value: " + heat;

        return heat;
    }
}
