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
 * Time: 1:55:41 PM<br>
 * </b>
 */
public class GunHeat extends GPRoboNode {
    private static final long serialVersionUID = -1456391828393656956L;

    public String toString() {
        return "gunHeat";
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
        double heat = Stuff.clampZero(getAgent().getGunHeat());
        assert !(Double.isNaN(heat) || Double.isInfinite(heat)) : "heat was bad!";
        assert Stuff.isReasonable(heat) : "unreasonable value: " + heat;
        data.d = heat;
    }
}
