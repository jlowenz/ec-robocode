package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.GeneticOperators;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.VetoTypeInduction;
import info.javelot.functionalj.Function2Impl;
import info.javelot.functionalj.FunctionException;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 24, 2006<br>
 * Time: 7:57:31 PM<br>
 * </b>
 */
public class FiringPairConstantMutation implements GeneticOperators.MutationFunc {
    public void mutate(Node node) {
        List<GeneticOperators.Link> fp = new ArrayList<GeneticOperators.Link>();
        GeneticOperators.filterLinks(fp, node, 0, new Function2Impl<Boolean, GeneticOperators.Link, Integer>() {
            public Boolean call(GeneticOperators.Link link, Integer integer) throws FunctionException {
                return link.child instanceof FiringPairConstant;
            }
        });
        if (fp.size() > 0) {
            GeneticOperators.Link link = fp.get(GeneticOperators.rand.nextInt(fp.size()));
            FiringPair p = (FiringPair)link.child.evaluate();
            double newEnergy = p.getSecond().doubleValue() + (GeneticOperators.rand.nextDouble() * 2.0 - 1.0);
            newEnergy = (newEnergy < 0.0) ? 0.0 : newEnergy;
            try {
                link.parent.attach(link.childIndex, new FiringPairConstant((boolean) p.getFirst(), newEnergy));
            } catch (VetoTypeInduction vetoTypeInduction) {
                vetoTypeInduction.printStackTrace();
            }
        }
    }
}
