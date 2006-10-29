package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 28, 2006<br>
 * Time: 10:35:06 PM<br>
 * </b>
 */
public class Divide extends AbstractNode {

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
        double x = ((Number)operands[0].evaluate()).doubleValue();
        double y = ((Number)operands[0].evaluate()).doubleValue();
        if (y == 0.0 || y == -0.0) return 0.0;
        return x / y;
    }
}
