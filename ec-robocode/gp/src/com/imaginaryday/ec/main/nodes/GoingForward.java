package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 6, 2006<br>
 * Time: 6:56:23 PM<br>
 * </b>
 */
public class GoingForward extends RoboNode {
    private static final long serialVersionUID = -2886080237772408270L;

    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "goingForward";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return Boolean.class;
    }

    @Override
    public Object evaluate() {
        return getOwner().isGoingForward();
    }
}
