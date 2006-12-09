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
 * Time: 12:12:50 PM<br>
 * </b>
 */
public class Not extends GPRoboNode {
    private static final long serialVersionUID = 7935550059895404219L;

    public String toString() {
        return "!";
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
        boolean result;
        PolyData data = (PolyData) input;
        children[0].eval(state, thread, input, stack, individual, problem);
        result = data.b;

        // calculate here
        data.b = !result;
    }
}
