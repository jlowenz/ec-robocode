package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

/**
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:25:30 PM
 */
public abstract class RoboNode extends AbstractNode {
	static protected Node[] NONE = new Node[0];

    public GPAgent getOwner() {
		return (GPAgent) owner;
	}
}
