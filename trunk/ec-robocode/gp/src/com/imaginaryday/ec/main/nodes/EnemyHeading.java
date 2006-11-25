package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 11:02:06 PM<br>
 * </b>
 */
public class EnemyHeading extends RoboNode {
    private static final long serialVersionUID = 5668242729671814439L;

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
        double heading = Stuff.clampZero(getOwner().getEnemyHeading());
        assert !(Double.isNaN(heading) || Double.isInfinite(heading)) : "heading was bad!";
        assert Stuff.isReasonable(heading) : "unreasonable value: " + heading;

        return heading;
    }
}
