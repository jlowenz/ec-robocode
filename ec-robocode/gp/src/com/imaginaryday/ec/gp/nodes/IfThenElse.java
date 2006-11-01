package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.VetoTypeInduction;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 31, 2006<br>
 * Time: 9:01:48 PM<br>
 * </b>
 */
public class IfThenElse extends AbstractNode {

    Node[] child = new Node[3];
    Class outputType = null;


    public void induceOutputType(Class type) throws VetoTypeInduction {
        if (outputType != null && !outputType.equals(type)) throw new VetoTypeInduction("Been there, done that. Already been induced, therefore my children are of the current type, cannot be changed");
        outputType = type;
    }

    protected Node[] children() {
        return child;
    }

    public String getName() {
        return "if-then-else";
    }

    public Class getInputType(int id) {
        switch (id) {
            case 0: return Boolean.class;
            default:
                return outputType;
        }
    }

    public Class getOutputType() {
        return outputType;
    }

    public Object evaluate() {
        boolean pred = (Boolean)child[0].evaluate();
        if (pred) {
            return child[1].evaluate();
        } else {
            return child[2].evaluate();
        }
    }
}
