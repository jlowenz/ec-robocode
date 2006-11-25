package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.GeneticOperators;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.VetoTypeInduction;
import info.javelot.functionalj.Function2Impl;
import info.javelot.functionalj.FunctionException;
import org.jscience.mathematics.vectors.VectorFloat64;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 24, 2006<br>
 * Time: 7:48:20 PM<br>
 * </b>
 */
public class VectorConstantMutation implements GeneticOperators.MutationFunc {
    public void mutate(Node node) {
        List<GeneticOperators.Link> vc = new ArrayList<GeneticOperators.Link>();
        GeneticOperators.filterLinks(vc, node, 0, new Function2Impl<Boolean, GeneticOperators.Link, Integer>() {
            public Boolean call(GeneticOperators.Link link, Integer integer) throws FunctionException {
                return link.child instanceof VectorConstant;
            }
        });
        if (vc.size() > 0) {
            GeneticOperators.Link l = vc.get(GeneticOperators.rand.nextInt(vc.size()));
            VectorFloat64 v = (VectorFloat64) l.child.evaluate();
            double newX = v.getValue(0) + (GeneticOperators.rand.nextDouble() * 2.0 - 1.0);
            double newY = v.getValue(1) + (GeneticOperators.rand.nextDouble() * 2.0 - 1.0);
            try {
                l.parent.attach(l.childIndex, new VectorConstant(VectorFloat64.valueOf(newX, newY)));
            } catch (VetoTypeInduction vetoTypeInduction) {
                vetoTypeInduction.printStackTrace();
            }
        }
    }
}
