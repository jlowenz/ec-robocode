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
 * Time: 1:33:13 PM<br>
 * </b>
 */
public class EnemyHeading extends GPRoboNode {
    private static final long serialVersionUID = -5748190046047330831L;

    public String toString() {
        return "enemyHeading";
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
        double heading = Stuff.clampZero(getAgent().getEnemyHeading());
        assert !(Double.isNaN(heading) || Double.isInfinite(heading)) : "heading was bad!";
        assert Stuff.isReasonable(heading) : "unreasonable value: " + heading;
        data.d = heading;
    }
}
