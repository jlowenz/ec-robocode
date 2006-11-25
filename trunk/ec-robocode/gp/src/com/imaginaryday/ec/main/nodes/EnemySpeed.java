package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 11:03:31 PM<br>
 * </b>
 */
public class EnemySpeed extends RoboNode {
    private static final long serialVersionUID = 7739218149367218744L;
    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "enemySpeed";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        double speed = Stuff.clampZero(getOwner().getEnemySpeed());
        assert !(Double.isNaN(speed) || Double.isInfinite(speed)) : "enemy speed was bad!";
        assert Stuff.isReasonable(speed) : "unreasonable value: " + speed;

        return speed;
    }
}
