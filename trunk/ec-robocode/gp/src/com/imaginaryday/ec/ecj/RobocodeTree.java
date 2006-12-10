package com.imaginaryday.ec.ecj;

import com.imaginaryday.ec.ecj.functions.GPRoboNode;
import ec.gp.GPTree;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 10, 2006<br>
 * Time: 12:39:00 AM<br>
 * </b>
 */
public class RobocodeTree extends GPTree {
    public GPRoboNode getRoot() {
        return (GPRoboNode) child;
    }
}
