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
 * Date: Dec 9, 2006$<br>
 * Time: 1:06:29 PM$<br>
 * </b>
 */
public class BulletEnergy extends GPRoboNode {

    public String toString() {
        return "bulletEnergy";
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
        double d = Stuff.clampZero(getAgent().getBulletEnergy());
        assert !(Double.isNaN(d) || Double.isInfinite(d)) : "energy was bad!";
        assert Stuff.isReasonable(d) : "unreasonable value: " + d;
        data.d = d;
    }
}
