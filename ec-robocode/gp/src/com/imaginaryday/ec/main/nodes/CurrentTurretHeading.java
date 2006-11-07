package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Nov 6, 2006
 * Time: 5:47:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class CurrentTurretHeading extends RoboNode {

	protected Node[] children() {
		return NONE;
	}

	public String getName() {
		return "currentTurretHeading";
	}

	public Class getInputType(int id) {
		return null;
	}

	public Class getOutputType() {
		return Number.class;
	}

	public Object evaluate() {
		return null;
	}
}
