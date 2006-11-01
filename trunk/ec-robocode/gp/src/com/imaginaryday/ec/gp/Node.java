package com.imaginaryday.ec.gp;

/**
 * User: jlowens
 * Date: Oct 27, 2006
 * Time: 11:36:18 AM
 */
public interface Node {

	void setOwner(Object owner);

    String getName();

    Node attach(int id, Node n);
	int getInputCount();
	Class getInputType(int id);
    Class[] getInputTypes();

    Class getOutputType();

	Object evaluate();

    boolean isTerminal();
}
