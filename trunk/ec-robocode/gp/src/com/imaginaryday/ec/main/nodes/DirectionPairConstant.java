package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.VectorUtils;
import org.jscience.mathematics.vectors.VectorFloat64;

import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 30, 2006<br>
 * Time: 9:48:17 PM<br>
 * </b>
 */
public class DirectionPairConstant extends RoboNode {
    private static final long serialVersionUID = -7937075740723060075L;
    private static final Random rand = new Random();
    private DirectionPair p;

    public DirectionPairConstant() {
        p = new DirectionPair(VectorUtils.randomVector(), rand.nextBoolean());
    }
    protected DirectionPairConstant(DirectionPair p) {
        this.p = p;
    }
    public DirectionPairConstant(VectorFloat64 v, boolean b) {
        this.p = new DirectionPair(v, b);
    }

    @Override
    public <T extends Node> T copy() {
        return (T) new DirectionPairConstant(p);
    }
    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return p.first().toString() + ", " + p.second();
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return DirectionPair.class;
    }

    @Override
    public Object evaluate() {
        return p;
    }

    @Override
    protected String getConstructorParam() {
        return "VectorFloat64.valueOf(" + p.first().getValue(0) + "," + p.first().getValue(1) + ")," + Boolean.toString(p.second());
    }
}
