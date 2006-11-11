package com.imaginaryday.ec.gp;

import static com.imaginaryday.util.Collections.toList;
import info.javelot.functionalj.tuple.Pair;

import java.util.List;

/**
 * User: jlowens
 * Date: Oct 28, 2006
 * Time: 4:22:09 PM
 */
public abstract class AbstractNode implements Node {

	protected transient Object owner;
    protected abstract Node[] children();
    protected Pair<Class,Class[]> type = new Pair<Class, Class[]>(getOutputType(), getInputTypes());

    public <T extends Node> T copy() {
        // TODO: solve this with a new abstract method (e.g. create)
        try {
            //noinspection unchecked
            T root = (T)getClass().newInstance();
            copyChildren(root);
            return root;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }
        return null;
    }

    protected void copyChildren(Node n) throws VetoTypeInduction {
        int i = 0;
        for (Node c : children()) {
            n.attach(i++, c.copy());
        }
    }

    public void setOwner(Object owner) {
        if (this.owner == null || !this.owner.equals(owner)) {
            this.owner = owner;
		    for (Node n : children()) n.setOwner(this.owner);
        }
    }

    public Object getOwner() {
        return owner;
    }

    public Node getChild(int i) {
        return children()[i];
    }
    public List<Node> childList() {
        return toList(children());
    }

    public void induceOutputType(Class type) throws VetoTypeInduction {}

    public Node attach(int id, Node n) throws VetoTypeInduction {
        n.induceOutputType(getOutputType());
        children()[id] = n;
        return this;
    }

    public Class[] getInputTypes() {
        Class[] types = new Class[getInputCount()];
        for (int i = 0; i < getInputCount(); i++) types[i] = getInputType(i);
        return types;
    }

    public Pair<Class,Class[]> getType() {
        return type;
    }

    public int getInputCount() {
        return children().length;
    }

    public Object evaluate() {
        throw new RuntimeException("Unimplemented evaluate() method!");
    }

    public boolean isTerminal() {
        return getInputCount() == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getName()).append(" ");
	    Node children[] = children();
	    for (int i = 0; i < children.length; i++) {
            sb.append(children[i].toString());
		    if (i < children.length-1) sb.append(" ");
	    }
        sb.append(")");
        return sb.toString();
    }
}
