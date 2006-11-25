package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:47:24 PM<br>
 * </b>
 */
public class RamMyFault extends RoboNode {
    private static final long serialVersionUID = -2669229753842813336L;
    protected Node[] children() { return NONE; }
    public String getName() { return "ramMyFault"; }
    public Class getInputType(int id) { return null; }
    public Class getOutputType() { return Boolean.class; }

    public Object evaluate() {
        return getOwner().isMyFault();
    }
}
