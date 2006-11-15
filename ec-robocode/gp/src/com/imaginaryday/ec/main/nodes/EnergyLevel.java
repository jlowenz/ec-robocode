package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 9:32:32 PM<br>
 * </b>
 */
public class EnergyLevel extends RoboNode {

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
        double energy = getOwner().getEnergy();
        assert !(Double.isNaN(energy) || Double.isInfinite(energy)) : "energy was bad!";
        return energy;               
    }
}
