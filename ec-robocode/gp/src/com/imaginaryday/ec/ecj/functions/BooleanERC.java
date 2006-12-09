package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
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
 * Time: 12:35:23 PM<br>
 * </b>
 */
public class BooleanERC extends GPRoboERC {
    boolean val;
    private static final long serialVersionUID = 1379064437285358045L;
    public String name() {
        return "boolConst";
    }
    public void resetNode(final EvolutionState state, int thread) {
        val = state.random[thread].nextBoolean();
    }


    @Override
    public void mutateERC(final EvolutionState state, final int thread) {
        super.mutateERC(state, thread);
        // TODO - implement this!
    }
    public boolean nodeEquals(final GPNode node) {
        if (node instanceof BooleanERC) {
            BooleanERC erc = (BooleanERC) node;
            return val == erc.val;
        }
        return false;
    }

    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
        ((PolyData)input).b = val;
    }

    public int nodeHashCode() {
        return (val ? 1 : 0);
    }
    public String encode() {
        return Code.encode(val);
    }
    public boolean decode(final DecodeReturn dret) {
        Code.decode(dret);
        val = dret.l != 0;
        return true;
    }
}
