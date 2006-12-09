package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
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
 * Time: 11:27:40 AM<br>
 * </b>
 */
public class And extends GPRoboNode {
    private static final long serialVersionUID = -8275174248895297827L;

    public String toString() {
        return "&";
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
                     final GPData input, // don't like this!!! :-( ptui
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem) {
        boolean result;
        PolyData data = (PolyData) input;
        children[0].eval(state, thread, input, stack, individual, problem);
        result = data.b;

        children[1].eval(state, thread, input, stack, individual, problem);
        // calculate here
        data.b = result && data.b;
    }
}
