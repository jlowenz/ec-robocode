package com.imaginaryday.ec.ecj;

import com.imaginaryday.ec.rcpatches.ECJRobocodeManager;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Problem;
import ec.coevolve.GroupedProblemForm;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 10:59:42 AM<br>
 * </b>
 */
public class RobocodeProblem extends Problem implements GroupedProblemForm {

    private transient ECJRobocodeManager manager;

    public void preprocessPopulation(final EvolutionState state, Population pop) {
        
    }

    public void postprocessPopulation(final EvolutionState state, Population pop) {

    }

    public void evaluate(final EvolutionState state,
                         final Individual[] ind,  // the individuals to evaluate together
                         final boolean[] updateFitness,  // should this individuals' fitness be updated?
                         final boolean countVictoriesOnly,  // update fitnesses only to reflect victories, rather than spreads
                         final int threadnum) {
        
    }
}
