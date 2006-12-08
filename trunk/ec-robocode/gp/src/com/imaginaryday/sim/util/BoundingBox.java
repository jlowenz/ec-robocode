package com.imaginaryday.sim.geometry;

import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 7, 2006<br>
 * Time: 6:12:51 PM<br>
 * </b>
 */
public class BoundingBox {
    private VectorFloat64 min;
    private VectorFloat64 max;


    public BoundingBox(VectorFloat64 min, VectorFloat64 max) {
        this.min = min;
        this.max = max;
    }
    public Vector getMax() {
        return max;
    }
    public Vector getMin() {
        return min;
    }

    public BoundingBox union(BoundingBox bb)
    {
        return null; // todo:
    }

    public boolean intersects(BoundingBox bb)
    {
        return false; // todo:
    }

    public BoundingBox getIntersection(BoundingBox bb)
    {
        return null; // todo:
    }
}
