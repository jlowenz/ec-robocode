package com.imaginaryday.ec.gp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * User: jlowens
 * Date: Oct 27, 2006
 * Time: 11:53:22 AM
 */
public class NodeTest extends TestCase {

	public NodeTest() {
	}

	public NodeTest(String string) {
		super(string);
	}

	public void testNodes()
	{
        NodeFactory nf = NodeFactory.getInstance();
        Node root = nf.create("add");
        root.attach(0, nf.create("mul").attach(0, nf.create("const", 5))
                                       .attach(1, nf.create("const", 12)))
            .attach(1, nf.create("const", 42));

        assertEquals(102.0, root.evaluate());
    }

    public void testTreeCreation()
    {
        NodeFactory nf = NodeFactory.getInstance();
        TreeFactory tf = new TreeFactory(nf);

        Node root = tf.generateRandomTree();
        Object result = root.evaluate();
        System.out.println(result);
    }

    public static Test suite()
	{
		TestSuite s = new TestSuite();
		s.addTest(new NodeTest("testNodes"));
        s.addTest(new NodeTest("testTreeCreation"));
        return s;
	}
}
