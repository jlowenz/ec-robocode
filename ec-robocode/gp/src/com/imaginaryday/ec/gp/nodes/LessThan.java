package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.util.Stuff;

/**
 * Created by IntelliJ IDEA. User: jlowens Date: Oct 30, 2006 Time: 6:30:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class LessThan extends AbstractNode {
    private static final long serialVersionUID = 1L;
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
        double x = ((Number) children[0].evaluate()).doubleValue();
        double y = ((Number) children[1].evaluate()).doubleValue();
//	    assert (!Double.isNaN(x) && !Double.isInfinite(x)) : "x was bad! from: " + children[0];
//	    assert (!Double.isNaN(y) && !Double.isInfinite(y)) : "y was bad! from: " + children[1];
        assert Stuff.isReasonable(x) : "unreasonable value: " + x;
        assert Stuff.isReasonable(y) : "unreasonable value: " + y;
        return x < y;
    }
}
