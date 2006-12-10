package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
import com.imaginaryday.util.Stuff;
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
 * Time: 2:34:52 PM<br>
 * </b>
 */
public class VectorToNearestWall extends GPRoboNode {
    private static final long serialVersionUID = 8181597429894749454L;

    public String toString() {
        return "vecToNearWall";
    }

    @Override
    public void checkConstraints(final EvolutionState state, final int tree, final GPIndividual typicalIndividual, final Parameter individualBase) {
        super.checkConstraints(state, tree, typicalIndividual, individualBase);
        if (children.length != 0) {
            state.output.error("incorrect # of children for " + toStringForError() + " at " + individualBase);
        }
    }

    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
        PolyData data = (PolyData) input;
        // do stuff here
        VectorFloat64 vec = getAgent().getVectorToNearWall();
        assert !(Double.isNaN(vec.getValue(0)) || Double.isInfinite(vec.getValue(0))) : "vecToNearWall.x was bad!";
        assert !(Double.isNaN(vec.getValue(1)) || Double.isInfinite(vec.getValue(1))) : "vecToNearWall.y was bad!";
        assert Stuff.isReasonable(vec.getValue(0)) : "unreasonable value: " + vec;
        assert Stuff.isReasonable(vec.getValue(1)) : "unreasonable value: " + vec;
        data.v = vec;
    }
}
