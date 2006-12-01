package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import static com.imaginaryday.util.VectorUtils.normalize;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 30, 2006<br>
 * Time: 10:54:08 PM<br>
 * </b>
 */
public class DotProduct extends RoboNode {
    private Node child[] = new Node[2];
    private static final long serialVersionUID = 5384441129094622738L;

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "dot";
    }
    public Class getInputType(int id) {
        return Vector.class;
    }
    public Class getOutputType() {
        return Number.class;
    }

    @Override
    public Object evaluate() {
        VectorFloat64 v1 = (VectorFloat64) child[0].evaluate();
        VectorFloat64 v2 = (VectorFloat64) child[1].evaluate();

        v1 = normalize(v1);
        v2 = normalize(v2);

        return v1.getValue(0)*v2.getValue(0) + v1.getValue(1)*v2.getValue(1);
    }
}
