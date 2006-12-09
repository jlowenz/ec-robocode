package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
import com.imaginaryday.ec.main.nodes.DirectionPair;
import com.imaginaryday.util.VectorUtils;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Code;
import ec.util.DecodeReturn;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 1:15:25 PM<br>
 * </b>
 */
public class DirectionPairERC extends GPRoboERC {
    DirectionPair val;

    public String name() {
        return "dirPairConst";
    }
    public void resetNode(final EvolutionState state, int thread) {
        val = new DirectionPair(VectorUtils.randomVector(), state.random[thread].nextBoolean());
    }


    @Override
    public void mutateERC(final EvolutionState state, final int thread) {
        super.mutateERC(state, thread);
        // TODO - implement this!
    }
    
    @Override
    public Object clone() {
        DirectionPairERC erc = (DirectionPairERC) super.clone();
        erc.val = new DirectionPair(val.first(), val.second());
        return erc;
    }

    public boolean nodeEquals(final GPNode node) {
        if (node instanceof DirectionPairERC) {
            DirectionPairERC erc = (DirectionPairERC) node;
            return val.equals(erc.val);
        }
        return false;
    }

    public int nodeHashCode() {
        return val.hashCode();
    }
    public String encode() {
        return Code.encode(val.first().getValue(0)) +
                Code.encode(val.first().getValue(1)) +
                Code.encode(val.second());
    }

    public boolean decode(final DecodeReturn dret) {
        double x, y;
        boolean dir;
        Code.decode(dret);
        x = dret.d;
        Code.decode(dret);
        y = dret.d;
        Code.decode(dret);
        dir = dret.l != 0;
        val = new DirectionPair(VectorFloat64.valueOf(x,y), dir);
        return true;
    }

    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
        PolyData data = (PolyData) input;
        data.dp = val;
    }
}
