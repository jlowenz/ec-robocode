package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.AbstractNode;
import robocode.AdvancedRobot;

/**
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:25:30 PM
 */
public abstract class RoboNode extends AbstractNode {
	public AdvancedRobot getOwner() {
		return (AdvancedRobot) owner;
	}
}
