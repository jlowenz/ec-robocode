package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
import static com.imaginaryday.util.VectorUtils.normalize;
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
 * Time: 1:30:24 PM<br>
 * </b>
 */
public class DotProduct extends GPRoboNode {
    private static final long serialVersionUID = -6364220675654652647L;

    public String toString() {
        return "dotProduct";
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
        result = data.v;

        children[1].eval(state, thread, input, stack, individual, problem);
        // calculate here
        VectorFloat64 v1 = normalize(result);
        VectorFloat64 v2 = normalize(data.v);

        data.d = v1.getValue(0)*v2.getValue(0) + v1.getValue(1)*v2.getValue(1);        
    }
}
