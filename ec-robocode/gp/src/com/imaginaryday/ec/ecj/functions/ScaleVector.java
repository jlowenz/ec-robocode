package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
import com.imaginaryday.util.Stuff;
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
 * Time: 2:14:26 PM<br>
 * </b>
 */
public class ScaleVector extends GPRoboNode {
    private static final long serialVersionUID = -5000480310973752067L;

    public String toString() {
        return "scaleVec";
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
        double val;
        PolyData data = (PolyData) input;
        children[1].eval(state, thread, input, stack, individual, problem);
        val = data.d;

        children[0].eval(state, thread, input, stack, individual, problem);
        assert !(Double.isNaN(val) || Double.isInfinite(val)) : "scalar value was bad!";
        assert Stuff.isReasonable(val) : "unreasonable value: " + val;

        VectorFloat64 vec = data.v;
        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vec.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vec.y was bad!";
        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;

        if (Stuff.nearZero(val)) {
            data.v = vec; // sanitize - can't scale a vector to zero!
            return;
        }

        VectorFloat64 newVec = vec.times(Float64.valueOf(val));
        assert !(Double.isNaN(newVec.getValue(0)) || Double.isInfinite(newVec.getValue(0))) : "newVec.x was bad!";
        assert !(Double.isNaN(newVec.getValue(1)) || Double.isInfinite(newVec.getValue(1))) : "newVec.y was bad!";
        assert Stuff.isReasonable(newVec.getValue(0)) : "unreasonable value: " + newVec;
        assert Stuff.isReasonable(newVec.getValue(1)) : "unreasonable value: " + newVec;

        data.v = newVec;
    }
}
