package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 12, 2006<br>
 * Time: 5:40:19 PM<br>
 * </b>
 */
public class VectorConstant extends RoboNode {
    private static final Random rand = new Random();
    VectorFloat64 val;

    public VectorConstant(VectorFloat64 val) {
        this.val = val;
    }
    public VectorConstant() {
        this.val = VectorFloat64.valueOf(filterZero(rand.nextInt(3)-1), filterZero(rand.nextInt(3)-1));
        while (this.val.getValue(0) == 0.0 && this.val.getValue(1) == 0.0) {
            this.val = VectorFloat64.valueOf(filterZero(rand.nextInt(3)-1), filterZero(rand.nextInt(3)-1));
        }
        this.val = this.val.times(Float64.valueOf(1.0/this.val.normValue()));
    }

    private double filterZero(double v) {
        return (Stuff.nearZero(v)) ? 0.0 : v;
    }

    @Override
    public <T extends Node> T copy() {
        return (T) new VectorConstant(val);
    }
    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "vectorConst";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return Vector.class;
    }
    @Override
    public Object evaluate() {
        return val;
    }
}
