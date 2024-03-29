package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 12, 2006<br>
 * Time: 3:56:50 PM<br>
 * </b>
 */
public class BooleanConstant extends AbstractNode {
    private static final Random rand = new Random();
    boolean val;
    private static final long serialVersionUID = 2453461045207100125L;

    public BooleanConstant(Boolean b)
    {
        val = b;
    }

    public BooleanConstant(boolean b)
    {
        val = b;
    }

    public BooleanConstant() {
        val = rand.nextBoolean();
    }

    public <T extends Node> T copy() {
        return (T) new BooleanConstant(val);
    }

    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "boolConst";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return Boolean.class;
    }
    public Object evaluate() {
        return val;
    }

    @Override
    public String toString() {
        return Boolean.toString(val);
    }


    @Override
    protected String getConstructorParam() {
        return Boolean.toString(val);
    }
}
