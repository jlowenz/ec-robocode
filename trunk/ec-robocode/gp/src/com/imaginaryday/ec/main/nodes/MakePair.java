package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 11, 2006<br>
 * Time: 10:29:26 PM<br>
 * </b>
 */
public class MakePair extends RoboNode {

    private Node[] child = new Node[2];
    private static final long serialVersionUID = -67139516800145620L;

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "makePair";
    }
    public Class getInputType(int id) {
        switch (id) {
            case 0: return Boolean.class;
            case 1: return Number.class;
        }
        return null;
    }
    public Class getOutputType() {
        return FiringPair.class;
    }

    public Object evaluate() {
        Boolean b = (Boolean)child[0].evaluate();
        Number n = (Number)child[1].evaluate();
        return new FiringPair(b,n);
    }
}
