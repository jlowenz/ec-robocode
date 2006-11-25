package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:24:02 PM<br>
 * </b>
 */
public class HitByBullet extends RoboNode {
    private static final long serialVersionUID = 6869073954918650904L;
    public String getName() { return "hitByBullet"; }
    public Class getInputType(int id) {  return null; }
    public Class getOutputType() { return Boolean.class; }
    protected Node[] children() { return NONE; }
    public Object evaluate() {
        return getOwner().getHitByBullet(); 
    }
}
