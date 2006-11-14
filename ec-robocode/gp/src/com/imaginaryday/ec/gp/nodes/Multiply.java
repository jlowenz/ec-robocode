package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 28, 2006<br>
 * Time: 10:11:30 PM<br>
 * </b>
 */
public class Multiply extends AbstractNode {

    private Node[] operands = new Node[2];

    protected Node[] children() {
        return operands;
    }

    public String getName() {
        return "mul";
    }

    public Class getInputType(int id) {
        return Number.class;
    }

    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        double x = ((Number)operands[0].evaluate()).doubleValue();
        double y = ((Number)operands[1].evaluate()).doubleValue();
	    assert (!Double.isNaN(x) && !Double.isInfinite(x)) : "x was bad! from: " + operands[0];
	    assert (!Double.isNaN(y) && !Double.isInfinite(y)) : "y was bad! from: " + operands[1];

        double result = x * y;
	    assert !(Double.isNaN(result) || Double.isInfinite(result)) : "multiply result was bad!";
        return result;
    }

}
