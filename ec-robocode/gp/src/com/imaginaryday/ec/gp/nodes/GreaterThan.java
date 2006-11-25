package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.util.Stuff;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:56:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GreaterThan extends AbstractNode {
	private Node[] children = new Node[2];
    private static final long serialVersionUID = 2422507265699110514L;

    protected Node[] children() {
		return children;
	}

	public String getName() {
		return ">";
	}

	public Class getInputType(int id) {
		return Number.class;
	}

	public Class getOutputType() {
		return Boolean.class;
	}

	public Object evaluate() {
		double x = ((Number)children[0].evaluate()).doubleValue();
		double y = ((Number)children[1].evaluate()).doubleValue();
        assert Stuff.isReasonable(x) : "unreasonable value: " + x;
        assert Stuff.isReasonable(y) : "unreasonable value: " + y;
        return x > y;
	}
}
