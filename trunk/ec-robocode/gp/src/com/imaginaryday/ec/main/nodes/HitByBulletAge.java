package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:27:22 PM<br>
 * </b>
 */
public class HitByBulletAge extends RoboNode {
    private static final long serialVersionUID = -5542163176524890718L;
    protected Node[] children() { return NONE; }
    public String getName() { return "hitByBulletAge"; }
    public Class getInputType(int id) { return null; }
    public Class getOutputType() { return Number.class; }
    public Object evaluate() { return getOwner().getHitByBulletAge(); }
}
