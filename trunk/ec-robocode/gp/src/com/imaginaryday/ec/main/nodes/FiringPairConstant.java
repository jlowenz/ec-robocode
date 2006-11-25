package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;

import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 24, 2006<br>
 * Time: 5:43:58 PM<br>
 * </b>
 */
public class FiringPairConstant extends RoboNode {
    private static Random rand = new Random();
    private boolean toFire;
    private double energy;
    private static final long serialVersionUID = 4828972562005715117L;


    public FiringPairConstant() {
        toFire = rand.nextBoolean();
        energy = Stuff.clampZero(rand.nextDouble() * 3.0);
    }

    public FiringPairConstant(boolean b, double val) {
        toFire = b;
        energy = val;
    }

    public FiringPairConstant(Boolean b, Number n) {
        toFire = b;
        energy = n.doubleValue();
    }
    @Override
    public <T extends Node> T copy() {
        return (T) new FiringPairConstant(toFire, energy);
    }
    protected Node[] children() {
        return NONE;
    }
    public String getName() {
        return "firingPairConst";
    }
    public Class getInputType(int id) {
        return null;
    }
    public Class getOutputType() {
        return FiringPair.class;
    }

    @Override
    public Object evaluate() {
        return new FiringPair(toFire, energy);
    }


    @Override
    public String toString() {
        return "[" + toFire + " | " + energy + "]";        
    }


    @Override
    protected String getConstructorParam() {
        return Boolean.toString(toFire) + "," + Double.toString(energy);
    }
}
