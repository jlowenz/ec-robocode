package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 30, 2006<br>
 * Time: 10:22:54 PM<br>
 * </b>
 */
public class NegateVector extends RoboNode {
    private static final long serialVersionUID = -7599977957517723897L;    

    private Node child[] = new Node[1];
    public String getName() {
        return "negVector";
    }
    public Class getInputType(int id) {
        return Vector.class;
    }
    public Class getOutputType() {
        return Vector.class;
    }
    protected Node[] children() {
        return child;
    }

    @Override
    public Object evaluate() {
        VectorFloat64 v = (VectorFloat64) child[0].evaluate();
        return VectorFloat64.valueOf(-v.getValue(0),-v.getValue(1));
    }
}
