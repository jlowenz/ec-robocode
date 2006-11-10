package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.util.VectorUtils;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 5:50:07 PM<br>
 * </b>
 */
public class VectorHeading extends RoboNode {
    private RoboNode child[] = new RoboNode[1];

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "vecHeading";
    }
    public Class getInputType(int id) {
        return VectorFloat64.class;
    }
    public Class getOutputType() {
        return Number.class;
    }

    public Object evaluate() {
        return VectorUtils.toAngle((VectorFloat64)child[0].evaluate());
    }
}
