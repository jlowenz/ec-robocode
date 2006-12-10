package com.imaginaryday.ec.ecj.functions;

import com.imaginaryday.ec.ecj.PolyData;
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
 * Time: 2:18:57 PM<br>
 * </b>
 */
public class VectorERC extends GPRoboERC {

    VectorFloat64 vec;
    private static final long serialVersionUID = 2079456050321716225L;
    public String name() {
        return "vectConst";
    }
    public void resetNode(final EvolutionState state, int thread) {
        vec = VectorUtils.randomVector();
    }
    public boolean nodeEquals(final GPNode node) {
        if (node instanceof VectorERC) {
            VectorERC erc = (VectorERC) node;
            return vec.equals(erc.vec);
        }
        return false;
    }


    @Override
    public void mutateERC(final EvolutionState state, final int thread) {
        // TODO: super.mutateERC(state, thread);
    }
    public int nodeHashCode() {
        return vec.hashCode();
    }
    public String encode() {
        return Code.encode(vec.getValue(0)) + Code.encode(vec.getValue(1));
    }
    public boolean decode(final DecodeReturn dret) {
        double x,y;
        Code.decode(dret);
        x = dret.d;
        Code.decode(dret);
        y = dret.d;
        vec = VectorFloat64.valueOf(x,y);
        return true;
    }

    public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
        PolyData data = (PolyData) input;
        data.v = vec;
    }
}
