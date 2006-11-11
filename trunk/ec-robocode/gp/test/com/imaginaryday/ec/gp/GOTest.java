package com.imaginaryday.ec.gp;

import info.javelot.functionalj.tuple.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 11, 2006<br>
 * Time: 12:22:09 AM<br>
 * </b>
 */
public class GOTest extends TestCase {

    public GOTest() {
    }
    public GOTest(String string) {
        super(string);
    }

    public void testCrossover() {
        GeneticOperators go = GeneticOperators.getInstance();
        NodeFactory nf = NodeFactory.getInstance();
        Node rootA = nf.create("add");
        Node rootB = nf.create("sub");
        try {
            rootA.attach(0, nf.create("mul").attach(0, nf.create("const", 5))
                                           .attach(1, nf.create("const", 12)))
                .attach(1, nf.create("const", 42));

            rootB.attach(0, nf.create("mul").attach(0, nf.create("const", 3))
                                            .attach(1, nf.create("div").attach(0, nf.create("const", 1))
                                                                       .attach(1, nf.create("const", 2))))
                 .attach(1, nf.create("add").attach(0, nf.create("const", 10))
                                            .attach(1, nf.create("const", 12)));
            
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }

        Pair<Node,Node> p = go.crossover(rootA, rootB);
        System.out.println(rootA);
        System.out.println(rootB);
        System.out.println(p.getFirst().toString());
        System.out.println(p.getSecond().toString());
    }

    public static Test suite()
    {
        TestSuite s = new TestSuite();
        s.addTest(new GOTest("testCrossover"));
        return s;
	}

}
