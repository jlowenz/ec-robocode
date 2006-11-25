package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

/**
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:58:49 PM
 */
public class And extends AbstractNode {
	private Node[] child = new Node[2];
    private static final long serialVersionUID = 5083161405597706323L;

    protected Node[] children() {
		return child;
	}

	public String getName() {
		return "and";
	}

	public Class getInputType(int id) {
		return Boolean.class;
	}

	public Class getOutputType() {
		return Boolean.class;
	}

	public Object evaluate() {
		boolean a = (Boolean)child[0].evaluate();
		boolean b = (Boolean)child[1].evaluate();
		return a && b;
	}
}
