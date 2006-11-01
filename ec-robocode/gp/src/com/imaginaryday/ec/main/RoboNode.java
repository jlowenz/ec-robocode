package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.AbstractNode;
import robocode.AdvancedRobot;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:25:30 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RoboNode extends AbstractNode {
	protected AdvancedRobot owner() {
		return (AdvancedRobot) owner;
	}
}
