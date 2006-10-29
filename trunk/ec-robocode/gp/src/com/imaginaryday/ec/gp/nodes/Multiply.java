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
        return x * y;
    }

}
