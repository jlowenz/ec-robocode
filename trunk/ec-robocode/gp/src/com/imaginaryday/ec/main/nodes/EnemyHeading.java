package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 11:02:06 PM<br>
 * </b>
 */
public class EnemyHeading extends RoboNode {

    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "enemyHeading";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        double heading = getOwner().getEnemyHeading();
        assert !(Double.isNaN(heading) || Double.isInfinite(heading)) : "heading was bad!";
        return heading;
    }
}
