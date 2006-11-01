package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:30:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class LessThan extends AbstractNode {
	private Node[] children = new Node[2];

	protected Node[] children() {
		return children;
	}

	public String getName() {
		return "<";
	}

	public Class getInputType(int id) {
		return Number.class;
	}

	public Class getOutputType() {
		return Boolean.class;
	}

	public Object evaluate() {
		double x = (Double)children[0].evaluate();
		double y = (Double)children[1].evaluate();
		return x < y;
	}
}
