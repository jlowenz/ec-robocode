package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:06:40 PM<br>
 * </b>
 */
public class HitWall extends RoboNode {
    private static final long serialVersionUID = -1578978702953995872L;
    protected Node[] children() {
        return NONE;
    }

    public String getName() {
        return "hitWall";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Boolean.class;
    }


    public Object evaluate() {
        return getOwner().getHitWall();
    }
}
