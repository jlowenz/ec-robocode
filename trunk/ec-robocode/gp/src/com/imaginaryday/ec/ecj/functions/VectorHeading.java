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
 * Time: 2:29:50 PM<br>
 * </b>
 */
public class VectorHeading extends GPRoboNode {
    private static final long serialVersionUID = -6044705927153913570L;

    public String toString() {
        return "vecHeading";
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
        VectorFloat64 vecIn;
        PolyData data = (PolyData) input;
        children[0].eval(state, thread, input, stack, individual, problem);
        vecIn = data.v;

        // calculate here
        assert !(Double.isNaN(vecIn.getValue(0)) || Double.isInfinite(vecIn.getValue(0))) : "x is bad: " + vecIn;
        assert !(Double.isNaN(vecIn.getValue(1)) || Double.isInfinite(vecIn.getValue(1))) : "y is bad: " + vecIn;
        assert Stuff.isReasonable(vecIn.getValue(0)) : "unreasonable value: " + vecIn;
        assert Stuff.isReasonable(vecIn.getValue(1)) : "unreasonable value: " + vecIn;


        double d = VectorUtils.toAngle(vecIn);
        assert !(Double.isNaN(d) || Double.isInfinite(d)) : "angle is bad! from: " + vecIn;
        assert Stuff.isReasonable(d) : "unreasonable value: " + d;

        data.d = d;

    }
}
