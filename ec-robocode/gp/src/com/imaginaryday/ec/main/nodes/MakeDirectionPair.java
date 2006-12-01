package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 30, 2006<br>
 * Time: 9:41:50 PM<br>
 * </b>
 */
public class MakeDirectionPair extends RoboNode {
    private Node[] child = new Node[2];
    private static final long serialVersionUID = 4939456244421506184L;

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "makeDirPair";
    }
    public Class getInputType(int id) {
        switch (id)
        {
            case 0: return Vector.class;
            default: return Boolean.class;
        }
    }
    public Class getOutputType() {
        return DirectionPair.class;
    }


    @Override
    public Object evaluate() {
        VectorFloat64 v = (VectorFloat64) child[0].evaluate();
        boolean b = (Boolean) child[1].evaluate();
        return new DirectionPair(v, b);
    }
}
