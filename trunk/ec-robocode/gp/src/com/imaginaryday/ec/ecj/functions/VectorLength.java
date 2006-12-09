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
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 2:31:34 PM<br>
 * </b>
 */
public class VectorLength extends GPRoboNode {

    public String toString() {
        return "vecLength";
    }

    @Override
    public void checkConstraints(final EvolutionState state, final int tree, final GPIndividual typicalIndividual, final Parameter individualBase) {
        super.checkConstraints(state, tree, typicalIndividual, individualBase);
        if (children.length != 1) {
            state.output.error("incorrect # of children for " + toStringForError() + " at " + individualBase);
        }
    }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem) {
        VectorFloat64 vec;
        PolyData data = (PolyData) input;
        children[0].eval(state, thread, input, stack, individual, problem);
        vec = data.v;

        // calculate here
        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vec.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vec.y was bad!";
        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;

        double length = VectorUtils.vecLength(vec);
        assert !(Double.isNaN(length) || Double.isInfinite(length)) : "length was bad! vec: " + vec;
        assert Stuff.isReasonable(length) : "unreasonable value: " + length;
        data.d = length;
    }
}
