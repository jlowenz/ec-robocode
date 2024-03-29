package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:38:18 PM<br>
 * </b>
 */
public class Rammed extends RoboNode {
    private static final long serialVersionUID = 6451662116413055579L;
    protected Node[] children() { return NONE; }
    public String getName() { return "rammed"; }
    public Class getInputType(int id) {return null;}
    public Class getOutputType() {return Boolean.class;}

    public Object evaluate() {
        return getOwner().getRammed();
    }
}
