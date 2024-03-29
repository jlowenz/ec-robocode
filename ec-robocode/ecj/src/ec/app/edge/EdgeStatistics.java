/*
Copyright 2006 by Sean Luke
Licensed under the Academic Free License version 3.0
See the file "LICENSE" for more information
*/


package ec.app.edge;

import ec.EvolutionState;
import ec.gp.koza.KozaStatistics;
import ec.simple.SimpleProblemForm;
import ec.util.Output;

/* 
 * EdgeStatistics.java
 * 
 * Created: Fri Nov  5 16:03:44 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class EdgeStatistics extends KozaStatistics
    {
    public void finalStatistics(final EvolutionState state, final int result)
        {
        // print out the other statistics
        super.finalStatistics(state,result);

        // we have only one population, so this is kosher
        ((SimpleProblemForm)(state.evaluator.p_problem.clone())).describe(
            best_of_run[0], state, 0, statisticslog,Output.V_NO_GENERAL);
        }

    }
