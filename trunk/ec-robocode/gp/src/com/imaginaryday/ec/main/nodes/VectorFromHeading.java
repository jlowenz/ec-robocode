package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;
import com.imaginaryday.util.VectorUtils;
import org.jscience.mathematics.vectors.Vector;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 30, 2006<br>
 * Time: 11:04:24 PM<br>
 * </b>
 */
public class VectorFromHeading extends RoboNode {
    private Node child[] = new Node[1];
    private static final long serialVersionUID = -4542891073751929808L;

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "vecFromHeading";
    }
    public Class getInputType(int id) {
        return Number.class;
    }
    public Class getOutputType() {
        return Vector.class;
    }

    @Override
    public Object evaluate() {
        double a = Stuff.clampZero(((Number)child[0].evaluate()).doubleValue());
        return VectorUtils.vecFromDir(a);
    }
}
