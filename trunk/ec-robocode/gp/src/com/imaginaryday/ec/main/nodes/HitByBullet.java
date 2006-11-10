package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:24:02 PM<br>
 * </b>
 */
public class HitByBullet extends RoboNode {
    public String getName() { return "hitByBullet"; }
    public Class getInputType(int id) {  return null; }
    public Class getOutputType() { return Boolean.class; }
    protected Node[] children() { return NONE; }
    public Object evaluate() { return getOwner().getHitByBullet(); }
}
