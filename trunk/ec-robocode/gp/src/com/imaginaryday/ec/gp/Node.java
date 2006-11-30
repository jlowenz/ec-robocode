package com.imaginaryday.ec.gp;

import com.imaginaryday.util.Tuple;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * User: jlowens
 * Date: Oct 27, 2006
 * Time: 11:36:18 AM
 */
public interface Node extends Serializable {

	void setOwner(Object owner);
    Object getOwner();

    String getName();
    Node getChild(int i);
    List<Node> childList();

    Node attach(int id, Node n) throws VetoTypeInduction;
    void induceOutputType(Class type) throws VetoTypeInduction;
    int getInputCount();
	Class getInputType(int id);
    List<Class> getInputTypes();
    Class getOutputType();
    Tuple.Two<Class,List<Class>> getType();

    Object evaluate();

    boolean isTerminal();

    <T extends Node> T copy();
    String debugString(Set<Node> set);
}
