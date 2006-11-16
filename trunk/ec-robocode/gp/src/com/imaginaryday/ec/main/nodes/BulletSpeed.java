package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.util.Stuff;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:35:52 PM<br>
 * </b>
 */
public class BulletSpeed extends RoboNode {
    protected Node[] children() { return NONE; }
    public String getName() { return "bulletSpeed"; }
    public Class getInputType(int id) {return null;}
    public Class getOutputType() {return Number.class;}
    public Object evaluate() {
        double speed = getOwner().getBulletSpeed();
        assert !(Double.isNaN(speed) || Double.isInfinite(speed)) : "speed was bad!";
        assert Stuff.isReasonable(speed) : "unreasonable value: " + speed;
        return speed;
    }
}
