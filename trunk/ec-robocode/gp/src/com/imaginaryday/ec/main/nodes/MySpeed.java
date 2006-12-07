package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 6, 2006<br>
 * Time: 6:45:53 PM<br>
 * </b>
 */
public class MySpeed extends RoboNode {
    private static final long serialVersionUID = -899731822224827106L;

    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "mySpeed";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return Number.class;
    }

    @Override
    public Object evaluate() {
        return getOwner().getVelocity();
    }
}
