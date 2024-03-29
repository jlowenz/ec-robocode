/*
Copyright 2006 by Sean Luke
Licensed under the Academic Free License version 3.0
See the file "LICENSE" for more information
*/


package ec.app.twobox.func;

import ec.EvolutionState;
import ec.Problem;
import ec.app.twobox.TwoBox;
import ec.app.twobox.TwoBoxData;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

/* 
 * H1.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class H1 extends GPNode
    {
    public String toString() { return "h1"; }

    public void checkConstraints(final EvolutionState state,
                                 final int tree,
                                 final GPIndividual typicalIndividual,
                                 final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length!=0)
            state.output.error("Incorrect number of children for node " + 
                               toStringForError() + " at " +
                               individualBase);
        }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
        TwoBoxData rd = ((TwoBoxData)(input));
        TwoBox tb = ((TwoBox)problem);
        rd.x = tb.inputsh1[tb.currentIndex];
        }
    }



