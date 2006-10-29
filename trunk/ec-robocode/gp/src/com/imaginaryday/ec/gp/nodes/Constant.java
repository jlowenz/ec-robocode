package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;

import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 28, 2006<br>
 * Time: 11:00:04 PM<br>
 * </b>
 */
public class Constant extends AbstractNode {
    private static double min = -10, max = 10;
    private static Random rand = new Random();
    private Object val;

    public Constant() {
        this.val = min + rand.nextDouble()*(max-min);
    }

    public Constant(Object val)
    {
        this.val = val;
    }

    // Wow. Wonderful. We need these to handle the test construction method.
    public Constant(Number val)
    {
        this.val = val;
    }
    public Constant(Byte val)
    {
        this.val = val;
    }
    public Constant(Short val)
    {
        this.val = val;
    }
    public Constant(Integer val)
    {
        this.val = val;
    }
    public Constant(Long val)
    {
        this.val = val;
    }
    public Constant(Float val)
    {
        this.val = val;
    }
    public Constant(Double val)
    {
        this.val = val;
    }

    public void setRange(double min, double max) {
        assert min < max;
        Constant.min = min;
        Constant.max = max;
    }

    protected Node[] children() {
        return new Node[0];
    }

    public String getName() {
        return "const";
    }

    public Class getInputType(int id) {
        return null;
    }

    public Class getOutputType() {
        return Number.class;
    }


    public Object evaluate() {
        return val;
    }
}
