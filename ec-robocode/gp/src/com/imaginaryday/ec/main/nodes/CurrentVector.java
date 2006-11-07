package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.ec.gp.Node;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 31, 2006<br>
 * Time: 9:53:51 PM<br>
 * </b>
 */
public class CurrentVector extends RoboNode {

    protected Node[] children() {
        return NONE;
    }

    public String getName() {
        return "currentVector";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return VectorFloat64.class;
    }

	public Object evaluate() {
		return getOwner().getCurrentVector();
	}
}
