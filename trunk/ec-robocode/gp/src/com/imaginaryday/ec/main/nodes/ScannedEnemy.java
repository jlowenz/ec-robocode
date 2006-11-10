package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 11:09:14 PM<br>
 * </b>
 */
public class ScannedEnemy extends RoboNode {
    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "scannedEnemy";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return Boolean.class;
    }

    public Object evaluate() {
        return getOwner().getScannedEnemy();
    }
}
