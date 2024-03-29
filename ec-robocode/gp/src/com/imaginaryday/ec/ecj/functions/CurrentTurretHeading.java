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
 * Time: 1:13:24 PM<br>
 * </b>
 */
public class CurrentTurretHeading extends GPRoboNode {
    private static final long serialVersionUID = -1827957969642428634L;

    public String toString() {
        return "currentTurretHeading";
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
        double gun = Stuff.clampZero(getAgent().getGunHeadingRadians());
        assert !(Double.isNaN(gun) || Double.isInfinite(gun)) : "gun heading was bad!";
        assert Stuff.isReasonable(gun) : "unreasonable value: " + gun;
        data.d = gun;
    }
}
