package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
import com.imaginaryday.ec.main.nodes.FiringPair;
import com.imaginaryday.util.Stuff;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Code;
import ec.util.DecodeReturn;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 1:36:26 PM<br>
 * </b>
 */
public class FiringPairERC extends GPRoboERC {
    private boolean toFire = false;
    private double energy = 1.0;
    private static final long serialVersionUID = 4503288319704350517L;

    public String name() {
        return "firingPairERC";
    }
    public void resetNode(final EvolutionState state, int thread) {
        toFire = state.random[thread].nextBoolean();
        energy = Stuff.clampZero(state.random[thread].nextDouble() * 3.0);
    }

    public boolean nodeEquals(final GPNode node) {
        if (node instanceof FiringPairERC) {
            FiringPairERC erc = (FiringPairERC) node;
            return Stuff.close(energy, erc.energy) && toFire == erc.toFire;
        }
        return false;
    }


    @Override
    public void mutateERC(final EvolutionState state, final int thread) {
        super.mutateERC(state, thread);
        // TODO - implement this!
    }

    public int nodeHashCode() {
        int result;
        long temp;
        result = (toFire ? 1 : 0);
        temp = energy != +0.0d ? Double.doubleToLongBits(energy) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String encode() {
        return Code.encode(toFire) + Code.encode(energy);
    }
    public boolean decode(final DecodeReturn dret) {
        Code.decode(dret);
        toFire = dret.l != 0L;
        Code.decode(dret);
        energy = dret.d;
        return true;
    }

    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
        PolyData data = (PolyData) input;
        data.fp = new FiringPair(toFire, energy);
    }
}
