package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.util.Stuff;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 28, 2006<br>
 * Time: 9:56:59 PM<br>
 * </b>
 */
public class Add extends AbstractNode {
    
    private Node[] operands = new Node[2];


    public String getName() {
        return "add";
    }

    protected Node[] children() {
        return operands;
    }

    public Class getInputType(int idx) {
        return Number.class;
    }

    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        double x = ((Number)operands[0].evaluate()).doubleValue();
        double y = ((Number)operands[1].evaluate()).doubleValue();
        assert Stuff.isReasonable(x) : "unreasonable value: " + x;
        assert Stuff.isReasonable(y) : "unresaonable value: " + y;
//      assert (!Double.isNaN(x) && !Double.isInfinite(x)) : "x was bad! from: " + operands[0];
//	    assert (!Double.isNaN(y) && !Double.isInfinite(y)) : "y was bad! from: " + operands[1];
	    double result = x + y;
//	    assert !(Double.isNaN(result) || Double.isInfinite(result)) : "addition result was bad!";
        assert Stuff.isReasonable(result) : "unreasonable value: " + result;
        return result;
    }

    public String toString() {
        return "(add " + operands[0].toString() + " " + operands[1].toString() + ")";
    }
}
