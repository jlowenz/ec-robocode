package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.util.Stuff;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:34:00 PM<br>
 * </b>
 */
public class BulletEnergy extends RoboNode {
    protected Node[] children() { return NONE; }
    public String getName() { return "bulletEnergy"; }
    public Class getInputType(int id) { return null; }
    public Class getOutputType() { return Number.class; }
    public Object evaluate() {
        double d = Stuff.clampZero(getOwner().getBulletEnergy());
        assert !(Double.isNaN(d) || Double.isInfinite(d)) : "energy was bad!";
        assert Stuff.isReasonable(d) : "unreasonable value: " + d;
        return d;
    }
}
