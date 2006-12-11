package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
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
 * Time: 12:14:25 PM<br>
 * </b>
 */
public class NumberERC extends GPRoboERC {
    private static final long serialVersionUID = 8532391175442629572L;
    private static final double min = -10, max = 10;

    private double val = 0.0;

    public String name() {
        return "const";
    }
    public void resetNode(final EvolutionState state, int thread) {
        val = min + state.random[thread].nextDouble()*(max-min);
    }


    @Override
    public void mutateERC(final EvolutionState state, final int thread) {
        super.mutateERC(state, thread);
        // TODO - implement this!
    }
    
    public boolean nodeEquals(final GPNode node) {
        if (node instanceof NumberERC) {
            NumberERC erc = (NumberERC) node;
            return Stuff.close(val,erc.val);
        }
        return false;
    }

    public int nodeHashCode() {
        double blah = val;
        if (blah < 0) {
            blah -= blah * 0.0001;
        } else {
            blah += blah * 0.0001;
        }
        long temp = blah != +0.0d ? Double.doubleToLongBits(blah) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }
    
    public String encode() {
        return Code.encode(val);
    }
    public boolean decode(final DecodeReturn dret) {
        Code.decode(dret);
        val = dret.d;
        return true;
    }

    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
        PolyData data = (PolyData) input;
        data.d = val;
    }
}
