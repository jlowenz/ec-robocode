package ec.gp;

/**
 * User: jlowens
 * Date: Oct 27, 2006
 * Time: 11:36:18 AM
 */
public interface Node {

	Node setInput(int id, Node n);
	int getInputCount();
	Type getInputType();

	Type getOutputType();

	Object evaluate();
}
