package ec.gp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Oct 27, 2006
 * Time: 11:53:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class NodeTest extends TestCase {

	public NodeTest() {
	}

	public NodeTest(String string) {
		super(string);
	}

	public void testNodes()
	{

	}

	public static Test suite()
	{
		TestSuite s = new TestSuite();
		s.addTest(new NodeTest("testNodes"));
		return s;
	}
}
