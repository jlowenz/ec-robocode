package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import info.javelot.functionalj.tuple.Pair;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 11, 2006<br>
 * Time: 10:29:26 PM<br>
 * </b>
 */
public class MakePair extends RoboNode {

    private Node[] child = new Node[2];

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
        return Pair.class;
    }

    public Object evaluate() {
        Boolean b = (Boolean)child[0].evaluate();
        Number n = (Number)child[1].evaluate();
        return new Pair<Boolean,Number>(b,n);
    }
}
