package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

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
    public Object evaluate() {return getOwner().getBulletEnergy();}
}
