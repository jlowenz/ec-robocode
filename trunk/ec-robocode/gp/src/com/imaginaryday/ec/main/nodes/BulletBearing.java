package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:29:10 PM<br>
 * </b>
 */
public class BulletBearing extends RoboNode {
    protected Node[] children() { return NONE; }
    public String getName() { return "bulletBearing"; }
    public Class getInputType(int id) { return null; }
    public Class getOutputType() { return Number.class; }
    public Object evaluate() {
        double b = getOwner().getBulletBearing();
        assert !(Double.isNaN(b) || Double.isInfinite(b)) : "bearing is bad!";
        assert (b >= 0.0 && b <= 2*Math.PI) : "bearing is out of bounds!";
        return b;
    }
}
