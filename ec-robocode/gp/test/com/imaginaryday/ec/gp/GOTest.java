package com.imaginaryday.ec.gp;

import static com.imaginaryday.ec.gp.GeneticOperators.*;
import com.imaginaryday.util.Tuple;
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

        Tuple.Two<Node,Node> p = go.crossover(pseudoRoot(rootA), pseudoRoot(rootB));
        System.out.println(rootA);
        System.out.println(rootB);
        System.out.println(p.getFirst().getChild(0).toString());
        System.out.println(p.getSecond().getChild(0).toString());
    }

    public void testRandomCrossover() {
        GeneticOperators go = GeneticOperators.getInstance();
        NodeFactory nf = NodeFactory.getInstance();
        TreeFactory tf = new TreeFactory(nf);

        for (int i = 0; i < 100; i++) {
            Node a = tf.generateRandomTree(5, Number.class);
            Node b = tf.generateRandomTree(5, Number.class);

            System.out.println(a);
            System.out.println(b);
            Tuple.Two<Node,Node> res = go.crossover(pseudoRoot(a), pseudoRoot(b));
            System.out.println(res.getFirst().getChild(0));
            System.out.println(res.getSecond().getChild(0));
        }
    }

    public void testCycle()
    {
        System.err.println("TESTING CYCLE");
        runMutation(0);
    }

    public void testGrow()
    {
        System.err.println("TESTING GROW");
        runMutation(1, 100);
    }

    public void testShrink()
    {
        System.err.println("TESTING SHRINK");
        runMutation(2, 100);
    }

    public void testSwitch()
    {
        System.err.println("TESTING SWITCH");
        runMutation(3);
    }

    private void runMutation(int index) {
        runMutation(index, 5000);
    }

    private void runMutation(int index, int count) {
        GeneticOperators go = GeneticOperators.getInstance();
        NodeFactory nf = NodeFactory.getInstance();
        TreeFactory tf = new TreeFactory(nf);

        Node a = pseudoRoot(tf.generateRandomTree(4, Number.class));
        for (int i = 0; i < count; i++) {
            go.mutations.get(index).mutate(a);
            try {
                int blah = numLinks(a.getChild(0));
                int erg = depth(a.getChild(0));
                System.out.println(erg + " " + blah);
            } catch (Throwable t) {
                t.printStackTrace();
                fail();
            }
        }
    }

    public void testDepth() {
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

        assertEquals(3, depth(rootA));
        assertEquals(4, depth(rootB));
    }

    public void testNumLinks() {
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

        assertEquals(4, numLinks(rootA));
        assertEquals(8, numLinks(rootB));
        assertEquals(4, countLinks(rootA));
        assertEquals(8, countLinks(rootB));
    }


    public static Test suite()
    {
        TestSuite s = new TestSuite();
        s.addTest(new GOTest("testDepth"));
        s.addTest(new GOTest("testNumLinks"));
        s.addTest(new GOTest("testRandomCrossover"));
        s.addTest(new GOTest("testCycle"));
        s.addTest(new GOTest("testGrow"));
        s.addTest(new GOTest("testShrink"));
        s.addTest(new GOTest("testSwitch"));
        s.addTest(new GOTest("testCrossover"));
        return s;
	}

}
