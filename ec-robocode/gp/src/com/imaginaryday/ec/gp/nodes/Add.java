package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import static com.imaginaryday.util.Stuff.clampZero;
import static com.imaginaryday.util.Stuff.isReasonable;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 28, 2006<br>
 * Time: 9:56:59 PM<br>
 * </b>
 */
public class Add extends AbstractNode {
    
    private Node[] operands = new Node[2];
    private static final long serialVersionUID = -3658244387907158747L;


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
        double x = clampZero(((Number)operands[0].evaluate()).doubleValue());
        double y = clampZero(((Number)operands[1].evaluate()).doubleValue());
        assert isReasonable(x) : "unreasonable value: " + x;
        assert isReasonable(y) : "unresaonable value: " + y;
//      assert (!Double.isNaN(x) && !Double.isInfinite(x)) : "x was bad! from: " + operands[0];
//	    assert (!Double.isNaN(y) && !Double.isInfinite(y)) : "y was bad! from: " + operands[1];
	    double result = x + y;
//	    assert !(Double.isNaN(result) || Double.isInfinite(result)) : "addition result was bad!";
        assert isReasonable(result) : "unreasonable value: " + result;
        return result;
    }

}
