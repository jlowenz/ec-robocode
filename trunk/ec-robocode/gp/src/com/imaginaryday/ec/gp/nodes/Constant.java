package com.imaginaryday.ec.gp.nodes;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.VetoTypeInduction;
import com.imaginaryday.util.Stuff;

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
    private static final long serialVersionUID = -2363840530594256597L;


    @SuppressWarnings({"unchecked"})
    public <T extends Node> T copy() {
        T c = (T)new Constant(val);
        try {
            copyChildren(c);
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }
        return c;
    }

    public Constant() {
        double v = min + rand.nextDouble()*(max-min);
        if (Stuff.nearZero(v)) v = 0.0;
        this.val = v;
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

	public String toString() {
		return val.toString();
	}


    @Override
    protected String getConstructorParam() {
        return "new Double(" + val.toString() + ")";
    }
}
