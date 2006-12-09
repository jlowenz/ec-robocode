package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
import com.imaginaryday.util.Stuff;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 11:29:04 AM<br>
 * </b>
 */
public class Divide extends GPRoboNode {
    private static final long serialVersionUID = 5861452763492208529L;

    public String toString() {
        return "/";
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
        double result;
        PolyData data = (PolyData) input;
        children[0].eval(state, thread, input, stack, individual, problem);
        result = Stuff.clampZero(data.d);

        children[1].eval(state, thread, input, stack, individual, problem);
        // calculate here
        if (Stuff.nearZero(data.d)) data.d = 1.0;
        else data.d = result / data.d;
    }
}
