package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

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
        return x + y;
    }


    public String toString() {
        return "(add " + operands[0].toString() + " " + operands[1].toString() + ")";
    }
}
