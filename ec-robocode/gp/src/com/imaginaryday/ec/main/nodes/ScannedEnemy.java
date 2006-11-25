package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 11:09:14 PM<br>
 * </b>
 */
public class ScannedEnemy extends RoboNode {
    private static final long serialVersionUID = -5148563679007629310L;
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
