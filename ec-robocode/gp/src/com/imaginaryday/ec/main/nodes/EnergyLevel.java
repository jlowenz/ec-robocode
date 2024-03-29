package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 9:32:32 PM<br>
 * </b>
 */
public class EnergyLevel extends RoboNode {
    private static final long serialVersionUID = 2945701519370000295L;

    protected Node[] children() {
        return NONE;
    }

    public String getName() {
        return "energyLevel";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        double energy = Stuff.clampZero(getOwner().getEnergy());
        assert !(Double.isNaN(energy) || Double.isInfinite(energy)) : "energy was bad!";
        assert Stuff.isReasonable(energy) : "unreasonable value: " + energy;

        return energy;
    }
}
