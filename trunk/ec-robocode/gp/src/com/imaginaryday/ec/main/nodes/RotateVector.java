package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;
import com.imaginaryday.util.VectorUtils;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.Vector;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 3:07:56 PM<br>
 * </b>
 */
public class RotateVector extends RoboNode {
    private Node child[] = new Node[2];

    protected Node[] children() {
        return child;
    }
    public String getName() {
        return "rotateVector";
    }
    public Class getInputType(int id) {
        switch (id) {
            case 0: return Vector.class;
            case 1: return Number.class;
        }
        return null;
    }
    public Class getOutputType() {
        return Vector.class;
    }

    public Object evaluate() {
        VectorFloat64 vec = (VectorFloat64)child[0].evaluate();

        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vec.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vec.y was bad!";
        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;


        double len = VectorUtils.vecLength(vec);
        assert !(Double.isNaN(len) || Double.isInfinite(len)) : "vector length was bad! vec was: " + vec;
        assert Stuff.isReasonable(len) : "unreasonable value: " + len;

        double angle = ((Number)child[1].evaluate()).doubleValue();
        assert !(Double.isNaN(angle) || Double.isInfinite(angle)) : "angle parameter was bad!";
        assert Stuff.isReasonable(angle) : "unreasonable value: " + angle;

        double curAngle = VectorUtils.toAngle(vec);
        assert !(Double.isNaN(curAngle) || Double.isInfinite(curAngle)) : "curAngle was bad!";
        assert Stuff.isReasonable(curAngle) : "unreasonable value: " + curAngle;

        double newAngle = (curAngle + angle) % (2.0*Math.PI);
        assert !(Double.isNaN(newAngle) || Double.isInfinite(newAngle)) : "newAngle was bad!";
        assert Stuff.isReasonable(newAngle) : "unreasonable value: " + newAngle;

        VectorFloat64 newVec = VectorUtils.vecFromDir(newAngle).times(Float64.valueOf(len));
        assert !(Double.isNaN(newVec.getValue(0)) || Double.isInfinite(newVec.getValue(0))) : "newVec.x was bad!";
        assert !(Double.isNaN(newVec.getValue(1)) || Double.isInfinite(newVec.getValue(1))) : "newVec.y was bad!";
        assert Stuff.isReasonable(newVec.getValue(0)) : "unreasonable value: " + newVec;
        assert Stuff.isReasonable(newVec.getValue(1)) : "unreasonable value: " + newVec;

        return newVec;
    }
}
