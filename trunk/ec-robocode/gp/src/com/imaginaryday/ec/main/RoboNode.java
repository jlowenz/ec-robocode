package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.AbstractNode;
import ec.GPAgent;

/**
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:25:30 PM
 */
public abstract class RoboNode extends AbstractNode {

    public GPAgent getOwner() {
		return (GPAgent) owner;
	}
}
