package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 28, 2006<br>
 * Time: 10:05:32 PM<br>
 * </b>
 */
public class Subtract extends AbstractNode {

    private Node[] operands = new Node[2];


    public String getName() {
        return "sub";
    }

    protected Node[] children() {
        return operands;
    }

    public Class getInputType(int index) {
        return Number.class;
    }

    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        double x = ((Number)operands[0].evaluate()).doubleValue();
        double y = ((Number)operands[1].evaluate()).doubleValue();
        return x - y;
    }

}
