package com.imaginaryday.ec.gp.nodes;

import static com.imaginaryday.util.Stuff.clampZero;
import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.util.Stuff;

import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 28, 2006<br>
 * Time: 10:35:06 PM<br>
 * </b>
 */
public class Divide extends AbstractNode {
    private static Random rand = new Random();
    private Node[] operands = new Node[2];

    protected Node[] children() {
        return operands;
    }

    public String getName() {
        return "div";
    }

    public Class getInputType(int id) {
        return Number.class;
    }

    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        double x = clampZero(((Number)operands[0].evaluate()).doubleValue());
        double y = clampZero(((Number)operands[1].evaluate()).doubleValue());
//        assert !(Double.isNaN(x) || Double.isInfinite(x)) : "x is bad. from: " + operands[0];
//        assert !(Double.isNaN(y) || Double.isInfinite(y)) : "y is bad. from: " + operands[1];
        assert Stuff.isReasonable(x) : "unreasonable value: " + x;
        assert Stuff.isReasonable(y) : "unreasonable value: " + y;

        if (Stuff.nearZero(y)) return 1.0;
        double result = clampZero(x / y);
//        assert !(Double.isNaN(result) || Double.isInfinite(result)) : "division result is bad";
        assert Stuff.isReasonable(result) : "unreasonable value: " + result;
        return x / y;
    }
}
