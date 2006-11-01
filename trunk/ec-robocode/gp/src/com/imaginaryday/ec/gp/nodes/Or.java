package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 31, 2006<br>
 * Time: 8:45:45 PM<br>
 * </b>
 */
public class Or extends AbstractNode {

    private Node[] child = new Node[2];

    protected Node[] children() {
        return child;
    }

    public String getName() {
        return "or";
    }

    public Class getInputType(int id) {
        return Boolean.class;
    }

    public Class getOutputType() {
        return Boolean.class;
    }

    public Object evaluate() {
        boolean a = (Boolean)child[0].evaluate();
        boolean b = (Boolean)child[1].evaluate();
        return a || b;
    }
}
