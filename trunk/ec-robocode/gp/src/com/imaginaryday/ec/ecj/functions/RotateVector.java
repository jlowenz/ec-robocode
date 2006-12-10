package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
import com.imaginaryday.util.Stuff;
import com.imaginaryday.util.VectorUtils;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 2:12:25 PM<br>
 * </b>
 */
public class RotateVector extends GPRoboNode {
    private static final long serialVersionUID = -8232280374470603373L;

    public String toString() {
        return "rotateVector";
    }

    @Override
    public void checkConstraints(final EvolutionState state, final int tree, final GPIndividual typicalIndividual, final Parameter individualBase) {
        super.checkConstraints(state, tree, typicalIndividual, individualBase);
        if (children.length != 2) {
            state.output.error("incorrect # of children for " + toStringForError() + " at " + individualBase);
        }
    }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem) {
        VectorFloat64 result;
        PolyData data = (PolyData) input;
        children[0].eval(state, thread, input, stack, individual, problem);
        VectorFloat64 vec = data.v;

        children[1].eval(state, thread, input, stack, individual, problem);
        // calculate here

        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vec.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vec.y was bad!";
        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;


        double len = VectorUtils.vecLength(vec);
        assert !(Double.isNaN(len) || Double.isInfinite(len)) : "vector length was bad! vec was: " + vec;
        assert Stuff.isReasonable(len) : "unreasonable value: " + len;

        double angle = data.d;
        assert !(Double.isNaN(angle) || Double.isInfinite(angle)) : "angle parameter was bad!";
        assert Stuff.isReasonable(angle) : "unreasonable value: " + angle;

        double curAngle = VectorUtils.toAngle(vec);
        assert !(Double.isNaN(curAngle) || Double.isInfinite(curAngle)) : "curAngle was bad!";
        assert Stuff.isReasonable(curAngle) : "unreasonable value: " + curAngle;

        double newAngle = Stuff.modHeading(curAngle + angle);
        assert !(Double.isNaN(newAngle) || Double.isInfinite(newAngle)) : "newAngle was bad!";
        assert Stuff.isReasonable(newAngle) : "unreasonable value: " + newAngle;

        VectorFloat64 newVec = VectorUtils.vecFromDir(newAngle).times(Float64.valueOf(len));
        assert !(Double.isNaN(newVec.getValue(0)) || Double.isInfinite(newVec.getValue(0))) : "newVec.x was bad!";
        assert !(Double.isNaN(newVec.getValue(1)) || Double.isInfinite(newVec.getValue(1))) : "newVec.y was bad!";
        assert Stuff.isReasonable(newVec.getValue(0)) : "unreasonable value: " + newVec;
        assert Stuff.isReasonable(newVec.getValue(1)) : "unreasonable value: " + newVec;

        data.v = newVec;

    }
}
