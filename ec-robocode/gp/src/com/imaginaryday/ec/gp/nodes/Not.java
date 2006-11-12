package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 31, 2006<br>
 * Time: 8:57:23 PM<br>
 * </b>
 */
public class Not extends AbstractNode {

    private Node[] child = new Node[1];

    protected Node[] children() {
        return child;
    }

    public String getName() {
        return "not";
    }

    public Class getInputType(int id) {
        return Boolean.class;
    }

    public Class getOutputType() {
        return Boolean.class;
    }

    public Object evaluate() {
        return !((Boolean)child[0].evaluate());
    }
}
